import { test, expect } from '@playwright/test';

test.describe('Performance Budget Checks', () => {

    const PERFORMANCE_THRESHOLDS = {
        fcp: 1200, // First Contentful Paint <= 1.2s
        tbt: 200,  // Total Blocking Time <= 200ms
    };

    test('Homepage meets performance budgets', async ({ page }) => {
        // Inject PerformanceObserver to track long tasks for TBT calculation
        await page.addInitScript(() => {
            (window as any).__tbt = 0;
            const observer = new PerformanceObserver((list) => {
                for (const entry of list.getEntries()) {
                    // A long task is one that takes > 50ms. TBT is the sum of (duration - 50) for all long tasks before TTI
                    (window as any).__tbt += entry.duration - 50;
                }
            });
            observer.observe({ type: 'longtask', buffered: true });
            (window as any).__observer = observer;
        });

        await page.goto('/');

        // Wait for the network to be mostly idle so page has settled
        await page.waitForLoadState('networkidle');

        // Retrieve metrics
        const metrics = await page.evaluate(async () => {
            // 1. Get FCP
            const paintEntries = performance.getEntriesByType('paint');
            const fcpEntry = paintEntries.find(entry => entry.name === 'first-contentful-paint');
            const fcp = fcpEntry ? fcpEntry.startTime : 0;

            // 2. Stop observer and get TBT
            (window as any).__observer.disconnect();
            const tbt = (window as any).__tbt;

            return { fcp, tbt };
        });

        console.log(`[Homepage] FCP: ${Math.round(metrics.fcp)}ms, TBT: ${Math.round(metrics.tbt)}ms`);

        expect(metrics.fcp).toBeLessThanOrEqual(PERFORMANCE_THRESHOLDS.fcp);
        expect(metrics.tbt).toBeLessThanOrEqual(PERFORMANCE_THRESHOLDS.tbt);
    });
});
