import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {

  test('should navigate between login and register', async ({ page }) => {
    await page.goto('/login');
    await page.click('text=Register here');
    await expect(page).toHaveURL('/register');

    await page.click('text=Login here');
    await expect(page).toHaveURL('/login');
  });

  test('should show validation errors on login', async ({ page }) => {
    await page.goto('/login');

    const usernameInput = page.locator('#username');
    const submitBtn = page.locator('button[type="submit"]');

    // Touch and leave empty
    await usernameInput.focus();
    await usernameInput.blur();
    await expect(page.locator('text=Username is required.')).toBeVisible();

    await expect(submitBtn).toBeDisabled();
  });

  test('should login successfully (mocked delay)', async ({ page }) => {
    // Mock the Login API
    await page.route('**/api/auth/login', async route => {
      // simulate delay
      await new Promise(resolve => setTimeout(resolve, 500));
      await route.fulfill({
        json: {
          token: 'fake-jwt-token',
          username: 'testuser',
          roles: ['GM']
        }
      });
    });

    // Mock Campaign API (for GM Dashboard)
    await page.route('**/api/campaigns', async route => {
      await route.fulfill({ json: [] });
    });

    await page.goto('/login');

    await page.fill('#username', 'testuser');
    await page.fill('#password', 'password123');

    const submitBtn = page.locator('button[type="submit"]');
    await expect(submitBtn).toBeEnabled();

    await submitBtn.click();

    // Check for loading state text
    await expect(page.locator('text=Logging in...')).toBeVisible();

    // Should eventually redirect to dashboard
    await expect(page).toHaveURL('/dashboard', { timeout: 10000 });
    await expect(page.locator('h1')).toHaveText('GM Dashboard');
  });

  test('should register successfully (mocked delay)', async ({ page }) => {
    // Mock the Register API
    await page.route('**/api/auth/register', async route => {
      // simulate delay
      await new Promise(resolve => setTimeout(resolve, 500));
      await route.fulfill({
        status: 201,
        json: { message: 'User registered successfully' }
      });
    });

    await page.goto('/register');

    await page.fill('#username', 'TestUser');
    await page.fill('#email', 'newuser@example.com');
    await page.fill('#password', 'password123');
    await page.fill('#confirmPassword', 'password123');

    const submitBtn = page.locator('button[type="submit"]');
    await expect(submitBtn).toBeEnabled();

    await submitBtn.click();

    // Should eventually redirect to login
    await expect(page).toHaveURL(/\/login(\?.*)?$/, { timeout: 10000 });
  });

  test('should show password mismatch error on register', async ({ page }) => {
    await page.goto('/register');

    await page.fill('#password', 'password123');
    await page.fill('#confirmPassword', 'different');
    await page.locator('#confirmPassword').blur();

    await expect(page.locator('text=Passwords do not match.')).toBeVisible();
    await expect(page.locator('button[type="submit"]')).toBeDisabled();
  });
});
