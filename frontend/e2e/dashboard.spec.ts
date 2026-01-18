import { test, expect } from '@playwright/test';

test.describe('Dashboard Feature', () => {
  test('should load the dashboard', async ({ page }) => {
    // Mock Authentication
    await page.addInitScript(() => {
      window.localStorage.setItem('token', 'fake-jwt-token');
      window.localStorage.setItem('currentUser', JSON.stringify({ username: 'TestGM', role: 'GM' }));
    });

    // Mock Campaign API (for GM Dashboard)
    await page.route('**/api/campaigns', async route => {
      await route.fulfill({ json: [] });
    });

    await page.goto('/dashboard');
    await expect(page).toHaveURL('/dashboard');
    
    // Check for GM Dashboard title
    await expect(page.locator('h1')).toHaveText('GM Dashboard');
  });
});