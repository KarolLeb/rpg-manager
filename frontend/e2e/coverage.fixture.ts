import { test as base, expect } from '@playwright/test';
import { addCoverageReport } from 'monocart-reporter';
import { OpenApiCoverageTracker } from './utils/openapi-coverage-tracker';

export const test = base.extend({
    page: async ({ page, browserName }, use) => {
        // Track API Coverage unconditionally
        page.on('request', request => {
            OpenApiCoverageTracker.getInstance().registerRequest(request.method(), request.url());
        });

        if (browserName === 'chromium') {
            await page.coverage.startJSCoverage();
            await use(page);
            const coverage = await page.coverage.stopJSCoverage();
            await addCoverageReport(coverage, test.info());
        } else {
            await use(page);
        }
    }
});

export { expect };
