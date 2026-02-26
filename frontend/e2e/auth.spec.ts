import { test, expect } from './coverage.fixture';

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

  test('should login successfully (no mocks)', async ({ page }) => {
    await page.goto('/login');

    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');

    const submitBtn = page.locator('button[type="submit"]');
    await expect(submitBtn).toBeEnabled();

    await submitBtn.click();

    // Should eventually redirect to dashboard
    await expect(page).toHaveURL('/dashboard', { timeout: 10000 });
    await expect(page.locator('.gm-dashboard h1')).toHaveText('GM Dashboard');
  });

  test('should register successfully (no mocks)', async ({ page }) => {
    const timestamp = Date.now();
    const username = 'RegUser' + timestamp;

    await page.goto('/register');

    await page.fill('#username', username);
    await page.fill('#email', `user${timestamp}@example.com`);
    await page.fill('#password', 'password123');
    await page.fill('#confirmPassword', 'password123');

    const submitBtn = page.locator('button[type="submit"]');
    await expect(submitBtn).toBeEnabled();

    await submitBtn.click();

    // Should eventually redirect to login
    await expect(page).toHaveURL(/\/login(\?.*)?$/, { timeout: 10000 });

    // Try to login with new user
    await page.fill('#username', username);
    await page.fill('#password', 'password123');
    await page.click('button[type="submit"]');

    await expect(page).toHaveURL('/dashboard', { timeout: 10000 });
    await expect(page.locator('.player-dashboard h1')).toHaveText('Player Dashboard');
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
