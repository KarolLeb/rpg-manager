import { test, expect } from './coverage.fixture';

test.describe('Dashboard Feature', () => {
  test('should load the dashboard (no mocks)', async ({ page }) => {
    // 1. Logowanie jako GM
    await page.goto('/login');
    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    await expect(page).toHaveURL('/dashboard');

    // Check for GM Dashboard title
    await expect(page.locator('.gm-dashboard h1')).toHaveText('GM Dashboard');

    // Verify seeded campaign is visible
    await expect(page.locator('.campaign-card h3').first()).toContainText('Kampania Smoczej Lancy');
  });
});
