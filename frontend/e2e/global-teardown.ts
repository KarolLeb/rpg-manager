import { OpenApiCoverageTracker } from './utils/openapi-coverage-tracker';

async function globalTeardown() {
    OpenApiCoverageTracker.getInstance().reportCoverage();
}

export default globalTeardown;
