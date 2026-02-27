const fs = require('fs');
const path = require('path');

// Usage: node crap-analyzer.js <path-to-jacoco.xml>
const xmlPath = process.argv[2];

if (!xmlPath) {
    console.error('CRAP Analyzer: Error - No jacoco.xml path provided.');
    process.exit(1);
}

const resolvedPath = path.resolve(xmlPath);

if (!fs.existsSync(resolvedPath)) {
    console.warn(`CRAP Analyzer: Skipping. File not found: ${resolvedPath}`);
    process.exit(0);
}

const xmlData = fs.readFileSync(resolvedPath, 'utf8');

// A threshold above which the build will fail.
const CRAP_THRESHOLD = 30;

let failed = false;

// We match package -> class -> method
const packageRegex = /<package name="([^"]+)">([\s\S]*?)<\/package>/g;
const classRegex = /<class name="([^"]+)">([\s\S]*?)<\/class>/g;
const methodRegex = /<method name="([^"]+)" desc="[^"]*" line="([^"]*)">([\s\S]*?)<\/method>/g;
// Within method:
const counterRegex = /<counter type="([^"]+)" missed="(\d+)" covered="(\d+)"\/>/g;

console.log(`CRAP Analyzer: Scanning ${resolvedPath} for methods with CRAP > ${CRAP_THRESHOLD}...`);

let matchPackage;
while ((matchPackage = packageRegex.exec(xmlData)) !== null) {
    const packageName = matchPackage[1];
    const packageBody = matchPackage[2];

    let matchClass;
    while ((matchClass = classRegex.exec(packageBody)) !== null) {
        const className = matchClass[1].replace(/\//g, '.');
        const classBody = matchClass[2];

        let matchMethod;
        while ((matchMethod = methodRegex.exec(classBody)) !== null) {
            const methodName = matchMethod[1];
            const methodLine = matchMethod[2];
            const methodBody = matchMethod[3];

            // Extract complexity and coverage
            let compMissed = 0, compCovered = 0;
            let instMissed = 0, instCovered = 0;
            
            let matchCounter;
            while ((matchCounter = counterRegex.exec(methodBody)) !== null) {
                const type = matchCounter[1];
                const missed = parseInt(matchCounter[2], 10);
                const covered = parseInt(matchCounter[3], 10);

                if (type === 'COMPLEXITY') {
                    compMissed = missed;
                    compCovered = covered;
                } else if (type === 'INSTRUCTION') {
                    instMissed = missed;
                    instCovered = covered;
                }
            }

            const comp = compMissed + compCovered;
            const totalInst = instMissed + instCovered;
            
            // Skip methods with 0 complexity or 0 instructions (interfaces/abstracts)
            if (comp === 0 || totalInst === 0) continue;

            const cov = totalInst === 0 ? 0 : (instCovered / totalInst);
            
            // CRAP = comp^2 * (1 - cov)^3 + comp
            const crap = Math.pow(comp, 2) * Math.pow(1 - cov, 3) + comp;

            if (crap > CRAP_THRESHOLD) {
                console.error(`❌ CRAP Limit Exceeded: ${className}.${methodName} (Line ${methodLine})`);
                console.error(`   - CRAP Score: ${crap.toFixed(2)} (Limit: ${CRAP_THRESHOLD})`);
                console.error(`   - Complexity: ${comp}`);
                console.error(`   - Coverage:   ${(cov * 100).toFixed(1)}%`);
                failed = true;
            }
        }
    }
}

if (failed) {
    console.error(`\nCRAP Analyzer: FAILED - One or more methods exceeded the CRAP limit of ${CRAP_THRESHOLD}.`);
    process.exit(1);
} else {
    console.log(`CRAP Analyzer: PASSED - All methods are within acceptable CRAP limits.`);
    process.exit(0);
}
