const fs = require('fs');
const path = require('path');

const PIT_REPORT_PATH = path.join(__dirname, '../backend/target/pit-reports/mutations.xml');
const SONAR_REPORT_PATH = path.join(__dirname, '../backend/target/sonar-pitest.json');

async function convert() {
    if (!fs.existsSync(PIT_REPORT_PATH)) {
        console.error('PIT report not found at ' + PIT_REPORT_PATH);
        process.exit(1);
    }

    const xmlData = fs.readFileSync(PIT_REPORT_PATH, 'utf-8');
    
    // Simple regex-based parsing to avoid extra dependencies if xml2js is not available
    const mutations = [];
    const mutationRegex = /<mutation detected='(.*?)' status='(.*?)'.*?><sourceFile>(.*?)<\/sourceFile><mutatedClass>(.*?)<\/mutatedClass><mutatedMethod>(.*?)<\/mutatedMethod>.*?<lineNumber>(.*?)<\/lineNumber><mutator>(.*?)<\/mutator>.*?<description>(.*?)<\/description><\/mutation>/g;
    
    let match;
    while ((match = mutationRegex.exec(xmlData)) !== null) {
        const [_, detected, status, sourceFile, mutatedClass, mutatedMethod, lineNumber, mutator, description] = match;
        
        // We only care about mutations that survived or have no coverage (the "issues")
        if (status === 'SURVIVED' || status === 'NO_COVERAGE') {
            // Find the actual file path. PIT mutatedClass is dot-separated.
            const packagePath = mutatedClass.split('.').slice(0, -1).join('/');
            const filePath = `src/main/java/${packagePath}/${sourceFile}`;

            mutations.push({
                engineId: 'pitest',
                ruleId: mutator.split('.').pop(),
                severity: status === 'SURVIVED' ? 'MAJOR' : 'MINOR',
                type: 'CODE_SMELL',
                primaryLocation: {
                    message: `Mutation survived: ${description} (Status: ${status})`,
                    filePath: filePath,
                    textRange: {
                        startLine: parseInt(lineNumber)
                    }
                }
            });
        }
    }

    // Deduplicate rules for the "rules" block
    const usedRules = [...new Set(mutations.map(m => m.ruleId))];
    const rules = usedRules.map(ruleId => ({
        id: ruleId,
        name: `Pitest ${ruleId}`,
        description: `Pitest mutation: ${ruleId}`,
        engineId: 'pitest',
        cleanCodeAttribute: 'TESTED',
        impacts: [{
            softwareQuality: 'RELIABILITY',
            severity: 'MEDIUM'
        }]
    }));

    const sonarIssues = { 
        rules: rules,
        issues: mutations 
    };
    fs.writeFileSync(SONAR_REPORT_PATH, JSON.stringify(sonarIssues, null, 2));
    console.log(`Successfully converted ${mutations.length} mutations to Sonar Generic Issue format (with rules) at ${SONAR_REPORT_PATH}`);
}

convert().catch(console.error);
