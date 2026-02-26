const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const services = [
  { name: 'core', container: 'rpg-backend-core', port: 6300, module: 'backend-core' },
  { name: 'auth', container: 'rpg-backend-auth', port: 6301, module: 'backend-auth' },
  { name: 'admin', container: 'rpg-backend-admin', port: 6302, module: 'backend-admin' }
];

const rootDir = path.join(__dirname, '../../');
const backendDir = path.join(rootDir, 'backend');

console.log('Dumping backend coverage from E2E runs (INSIDE containers)...');

let allPassed = true;

services.forEach(service => {
  console.log(`\n--- Processing ${service.name} ---`);
  try {
    const execFile = `/app/${service.module}/target/jacoco-e2e.exec`;
    const reportDir = `/app/${service.module}/target/site/jacoco-e2e`; // Custom report dir inside container

    // 1. Dump coverage data inside container
    console.log(`Dumping coverage to ${execFile}...`);
    execSync(`docker exec ${service.container} mvn org.jacoco:jacoco-maven-plugin:0.8.12:dump -Djacoco.address=localhost -Djacoco.port=${service.port} -Djacoco.destFile=${execFile}`, { stdio: 'inherit' });

    // 2. Generate XML report AND ENFORCE thresholds (60% bundle, 50% class) inside container
    console.log(`Generating XML report and checking thresholds (60% bundle, 50% class) in ${reportDir}...`);
    try {
      execSync(`docker exec ${service.container} mvn jacoco:report jacoco:check -Pe2e-check`, { stdio: 'inherit' });
    } catch (checkError) {
      console.error(`\n[!] Threshold check FAILED for ${service.name}. Check (60% bundle / 50% class) coverage.`);
      allPassed = false;
    }

    // 3. Copy report to host for separate Sonar project
    const hostReportDir = path.join(backendDir, `${service.module}/target/site/jacoco-e2e`);
    if (fs.existsSync(hostReportDir)) {
      fs.rmSync(hostReportDir, { recursive: true, force: true });
    }
    fs.mkdirSync(hostReportDir, { recursive: true });
    
    console.log(`Copying report to host: ${hostReportDir}...`);
    execSync(`docker cp ${service.container}:${reportDir}/. "${hostReportDir}"`, { stdio: 'inherit' });

  } catch (error) {
    console.error(`Failed to process coverage for ${service.name}:`, error.message);
    allPassed = false;
  }
});

if (!allPassed) {
  console.log('\n[!] Backend E2E coverage failed to meet quality standards (60% bundle / 50% class).');
  process.exit(1);
}

console.log('\nBackend E2E coverage processing complete. (60% bundle / 50% class) thresholds met.');
