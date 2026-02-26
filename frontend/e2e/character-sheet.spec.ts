import { test, expect } from './coverage.fixture';

test.describe('Character Sheet Feature (No Mocks)', () => {
  test.describe.configure({ mode: 'serial' });


  test.beforeEach(async ({ page }) => {
    // 1. Logowanie jako Gracz (player1 owns Geralt)
    await page.goto('/login');
    await page.fill('#username', 'player1');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    // Wait for redirect to dashboard
    await expect(page).toHaveURL('/dashboard');
  });

  test('should load character data', async ({ page }) => {
    // Navigate to character sheet
    await page.goto('/dashboard');
    const characterCard = page.locator('.character-card', { hasText: /Geralt/ }).first();
    await expect(characterCard).toBeVisible();
    await characterCard.getByRole('link', { name: 'View Sheet' }).click();

    // Check if the name field is populated
    const nameInput = page.locator('input[formControlName="name"]');
    await expect(nameInput).toBeVisible({ timeout: 10000 });
    await page.screenshot({ path: 'screenshot.png', fullPage: true });
    await expect(nameInput).toHaveValue(/Geralt/, { timeout: 10000 });

    // Check if attributes are loaded
    await expect(page.getByText('SIŁA', { exact: true })).toBeVisible();
  });

  test('should update and save character data', async ({ page }) => {
    await page.goto('/dashboard');
    await page.locator('.character-card', { hasText: /Geralt/ }).first().getByRole('link', { name: 'View Sheet' }).click();

    const timestamp = Date.now();
    const updatedName = 'Geralt ' + timestamp;

    // Change Name
    const nameInput = page.locator('input[formControlName="name"]');
    await nameInput.fill(updatedName);

    // Click Save
    const saveButton = page.locator('.save-btn', { hasText: 'ZAPISZ' });
    await expect(saveButton).toBeVisible();
    await saveButton.click();

    // Wait for Toast success message
    const toast = page.locator('app-toast .toast-item.success');
    await expect(toast).toBeVisible({ timeout: 10000 });
    await expect(toast).toContainText('Character saved successfully');

    // Refresh and verify
    await page.reload();
    await expect(nameInput).toHaveValue(updatedName);

    // Restore name for next runs (optional but good practice)
    await nameInput.fill('Geralt');
    await saveButton.click();
    await expect(toast).toBeVisible();
  });
});
