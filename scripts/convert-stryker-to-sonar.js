const fs = require('fs');
const path = require('path');

const STRYKER_REPORT_PATH = path.join(__dirname, '../frontend/reports/mutation/mutation.json');
const SONAR_REPORT_PATH = path.join(__dirname, '../frontend/stryker-report.json');

async function convert() {
    if (!fs.existsSync(STRYKER_REPORT_PATH)) {
        console.error('Stryker report not found at ' + STRYKER_REPORT_PATH);
        process.exit(1);
    }

    const reportData = JSON.parse(fs.readFileSync(STRYKER_REPORT_PATH, 'utf-8'));
    const files = reportData.files;
    const issues = [];

    for (const [filePath, fileData] of Object.entries(files)) {
        if (!fileData.mutants) continue;

        for (const mutant of fileData.mutants) {
            // We only care about mutations that survived or have no coverage (the "issues")
            if (mutant.status === 'Survived' || mutant.status === 'NoCoverage') {
                issues.push({
                    engineId: 'stryker',
                    ruleId: mutant.mutatorName,
                    primaryLocation: {
                        message: `Mutation survived: ${mutant.description} (Status: ${mutant.status})`,
                        filePath: filePath,
                        textRange: {
                            startLine: mutant.location.start.line,
                            endLine: mutant.location.end.line
                        }
                    }
                });
            }
        }
    }

    // Deduplicate rules for the "rules" block
    const usedRules = [...new Set(issues.map(i => i.ruleId))];
    const rules = usedRules.map(ruleId => ({
        id: ruleId,
        name: `Stryker ${ruleId}`,
        description: `Stryker mutation: ${ruleId}`,
        engineId: 'stryker',
        cleanCodeAttribute: 'TESTED',
        impacts: [{
            softwareQuality: 'RELIABILITY',
            severity: 'MEDIUM'
        }]
    }));

    const sonarIssues = { 
        rules: rules,
        issues: issues 
    };
    fs.writeFileSync(SONAR_REPORT_PATH, JSON.stringify(sonarIssues, null, 2));
    console.log(`Successfully converted ${issues.length} mutations to Sonar Generic Issue format (with rules) at ${SONAR_REPORT_PATH}`);
}

convert().catch(console.error);
