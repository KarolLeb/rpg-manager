import { test, expect } from './coverage.fixture';

test.describe('Campaign CRUD (No Mocks)', () => {

  test.beforeEach(async ({ page }) => {
    // 1. Logowanie jako GM
    await page.goto('/login');
    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    // Wait for redirect to dashboard
    await expect(page).toHaveURL('/dashboard');

    // Go to campaigns
    await page.goto('/campaigns');
    await expect(page).toHaveURL('/campaigns');
  });

  test('should create, read, update, and delete a campaign', async ({ page }) => {
    const timestamp = Date.now();
    const campaignName = 'Integrational Campaign ' + timestamp;
    const campaignDesc = 'Description for ' + campaignName;
    const updatedName = campaignName + ' Updated';

    // --- CREATE ---
    await page.click('text=Create New Campaign');
    await expect(page).toHaveURL('/campaigns/new');

    await page.fill('input#name', campaignName);
    await page.fill('textarea#description', campaignDesc);
    await page.click('button[type="submit"]');

    // Verify redirection to list
    await expect(page).toHaveURL('/campaigns');

    // --- READ ---
    // Check if the new campaign is in the list
    const campaignCard = page.locator('.campaign-card', { hasText: campaignName });
    await expect(campaignCard).toBeVisible();
    await expect(campaignCard).toContainText(campaignDesc);

    // --- UPDATE ---
    await campaignCard.locator('.edit-btn').click();
    // URL should contain /edit. Regex matches /campaigns/<digits>/edit
    await expect(page).toHaveURL(/\/campaigns\/\d+\/edit/);

    // Check pre-filled values
    await expect(page.locator('input#name')).toHaveValue(campaignName);

    // Modify
    await page.fill('input#name', updatedName);
    await page.click('button[type="submit"]');

    // Verify redirection and updated value
    await expect(page).toHaveURL('/campaigns');
    // Find the card that has the updated name in its h3
    const updatedCard = page.locator('.campaign-card').filter({
      has: page.locator('h3', { hasText: new RegExp(`^${updatedName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}$`) })
    });
    await expect(updatedCard).toBeVisible();

    // Ensure the old name (as a title) is gone. 
    await expect(page.locator('.campaign-card h3', { hasText: new RegExp(`^${campaignName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}$`) })).not.toBeVisible();

    // --- DELETE ---
    // Setup dialog handler BEFORE the action that triggers it
    page.once('dialog', async dialog => {
      await dialog.accept();
    });

    await updatedCard.locator('.delete-btn').click();

    // Verify removed
    await expect(updatedCard).not.toBeVisible();
  });

  test('should validate required fields', async ({ page }) => {
    await page.click('text=Create New Campaign');

    // Touch the name field and leave it empty to trigger validation
    await page.locator('input#name').focus();
    await page.locator('input#name').blur();

    // Check for validation message.
    await expect(page.locator('text=Name is required')).toBeVisible();

    // Button should be disabled
    await expect(page.locator('button[type="submit"]')).toBeDisabled();
  });
});
