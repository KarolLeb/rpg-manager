import { test as base, expect } from '@playwright/test';
import { addCoverageReport } from 'monocart-reporter';

export const test = base.extend({
    page: async ({ page, browserName }, use) => {
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
