import { OpenApiCoverageTracker } from './utils/openapi-coverage-tracker';

async function globalSetup() {
    await OpenApiCoverageTracker.getInstance().loadSpecs();
}

export default globalSetup;
