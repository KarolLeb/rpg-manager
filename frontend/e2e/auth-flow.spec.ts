import { test, expect } from '@playwright/test';

test.describe('Authentication & Dashboard Flow (No Mocks)', () => {

  test('should login as GM and see GM Dashboard', async ({ page }) => {
    await page.goto('/login');

    // Fill login form
    await page.fill('input[formControlName="username"]', 'gamemaster');
    await page.fill('input[formControlName="password"]', 'password');

    // Submit
    await page.click('button[type="submit"]');

    // Expect redirect to dashboard
    await expect(page).toHaveURL('/dashboard');

    // Verify GM specific content
    await expect(page.locator('.gm-dashboard h1')).toHaveText('GM Dashboard');
    await expect(page.locator('.campaign-card h3')).toContainText('Kampania Smoczej Lancy');

    // Verify Navbar User Info
    await expect(page.locator('.user-info .username')).toContainText('gamemaster (GM)');
  });

  test('should show validation errors on invalid input', async ({ page }) => {
    await page.goto('/login');

    // Touch fields but leave empty
    await page.focus('input[formControlName="username"]');
    await page.locator('input[formControlName="username"]').blur();

    await expect(page.locator('.error-text')).toContainText('Username is required');
  });
});
