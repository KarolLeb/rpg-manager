import { test, expect } from '@playwright/test';

test.describe.skip('Real Authentication Flow (No Mocks)', () => {
  
  test.beforeEach(async ({ page }) => {
    // Optionally log browser console for debugging
    // page.on('console', msg => console.log('BROWSER LOG:', msg.text()));
    page.on('pageerror', err => console.log('BROWSER ERROR:', err.message));
  });

  test('should return 401 for invalid credentials from real backend', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('#username', 'nonexistent-user-' + Date.now());
    await page.fill('#password', 'wrong-password');
    
    const submitBtn = page.locator('button[type="submit"]');
    await submitBtn.click();
    
    // Check for error message
    await expect(page.getByText('Invalid username or password')).toBeVisible({ timeout: 15000 });
  });

  test('should register and login successfully with real backend', async ({ page }) => {
    const timestamp = Date.now();
    const username = 'user' + timestamp;
    const email = 'user' + timestamp + '@example.com';
    const password = 'Password123!';

    // Register
    await page.goto('/register');
    await page.fill('#username', username);
    await page.fill('#email', email);
    await page.fill('#password', password);
    await page.fill('#confirmPassword', password);
    
    const regBtn = page.locator('button[type="submit"]');
    await expect(regBtn).toBeEnabled();
    await regBtn.click();

    // Wait for redirect to login
    await expect(page).toHaveURL(/\/login(\?.*)?$/, { timeout: 20000 });

    // Give it a small moment for Angular to stabilize
    await page.waitForTimeout(1000);

    // Login
    await page.locator('#username').fill(username);
    await page.locator('#password').fill(password);
    
    const loginBtn = page.locator('button[type="submit"]');
    await loginBtn.click();

    // Should redirect to dashboard
    await expect(page).toHaveURL('/dashboard', { timeout: 20000 });
    
    // Use specific locator to avoid strict mode violation (multiple headings)
    await expect(page.locator('h1')).toContainText('Dashboard', { timeout: 10000 });
  });
});