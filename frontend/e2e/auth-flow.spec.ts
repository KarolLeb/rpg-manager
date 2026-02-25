import { test, expect } from '@playwright/test';

test.describe('Authentication & Dashboard Flow', () => {

  test.beforeEach(async ({ page }) => {
    // Mock the Login API
    await page.route('**/api/auth/login', async route => {
      const json = {
        token: 'fake-jwt-token',
        username: 'TestGM',
        roles: ['GM']
      };
      await route.fulfill({ json });
    });

    // Mock Campaign API (for GM Dashboard)
    await page.route('**/api/campaigns', async route => {
      const json = [
        { id: 1, name: 'Curse of Strahd', description: 'Gothic Horror' }
      ];
      await route.fulfill({ json });
    });
  });

  test('should login as GM and see GM Dashboard', async ({ page }) => {
    await page.goto('/login');

    // Fill login form
    await page.fill('input[formControlName="username"]', 'TestGM');
    await page.fill('input[formControlName="password"]', 'password123');

    // Submit
    await page.click('button[type="submit"]');

    // Expect redirect to dashboard
    await expect(page).toHaveURL('/dashboard');

    // Verify GM specific content
    await expect(page.locator('h1')).toHaveText('GM Dashboard');
    await expect(page.locator('.campaign-card h3')).toHaveText('Curse of Strahd');

    // Verify Navbar User Info
    await expect(page.locator('.user-info .username')).toContainText('TestGM (GM)');
  });

  test('should show validation errors on invalid input', async ({ page }) => {
    await page.goto('/login');

    // Touch fields but leave empty
    await page.focus('input[formControlName="username"]');
    await page.locator('input[formControlName="username"]').blur();

    await expect(page.locator('.error-text')).toContainText('Username is required');
  });
});
