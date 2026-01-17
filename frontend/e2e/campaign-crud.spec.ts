import { test, expect } from '@playwright/test';

test.describe('Campaign CRUD', () => {
  // In-memory "database" for mocks
  let db: any[] = [];
  let nextId = 1;

  test.beforeEach(async ({ page }) => {
    // Reset DB and ID for each test to ensure isolation
    db = [];
    nextId = 1;

    // --- Mock API: Collection Resource ---
    await page.route('**/api/campaigns', async route => {
      const method = route.request().method();
      
      if (method === 'GET') {
        await route.fulfill({ json: db });
      } else if (method === 'POST') {
        const data = route.request().postDataJSON();
        const newCampaign = { 
          id: nextId++, 
          ...data, 
          status: 'DRAFT', 
          gameMasterName: 'GM' 
        };
        db.push(newCampaign);
        await route.fulfill({ json: newCampaign });
      } else {
        await route.continue();
      }
    });

    // --- Mock API: Item Resource ---
    await page.route(/.*\/api\/campaigns\/\d+$/, async route => {
      const method = route.request().method();
      const url = route.request().url();
      const id = parseInt(url.split('/').pop() || '0', 10);

      if (method === 'DELETE') {
        db = db.filter(c => c.id !== id);
        await route.fulfill({ status: 204 }); // No Content
      } else if (method === 'PUT') {
        const data = route.request().postDataJSON();
        const idx = db.findIndex(c => c.id === id);
        if (idx !== -1) {
          db[idx] = { ...db[idx], ...data };
          await route.fulfill({ json: db[idx] });
        } else {
          await route.fulfill({ status: 404 });
        }
      } else if (method === 'GET') {
        const campaign = db.find(c => c.id === id);
        if (campaign) {
          await route.fulfill({ json: campaign });
        } else {
          await route.fulfill({ status: 404 });
        }
      } else {
        await route.continue();
      }
    });

    // Start from the home page
    await page.goto('/');
    
    // Check if body is loaded
    await expect(page.locator('app-root')).toBeVisible();

    // Directly navigate to campaigns as a more robust alternative if menu click fails
    // or just use direct navigation to save time/flakiness if the menu is problematic
    await page.goto('/campaigns');
    await expect(page).toHaveURL('/campaigns');
  });

  test('should create, read, update, and delete a campaign', async ({ page }) => {
    const campaignName = 'Test Campaign ' + Date.now();
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
      has: page.locator('h3', { hasText: new RegExp(`^${updatedName}$`) }) 
    });
    await expect(updatedCard).toBeVisible();
    
    // Ensure the old name (as a title) is gone. 
    // Use regex for exact match.
    await expect(page.locator('.campaign-card h3', { hasText: new RegExp(`^${campaignName}$`) })).not.toBeVisible();

    // --- DELETE ---
    // Setup dialog handler BEFORE the action that triggers it
    page.once('dialog', async dialog => {
      // console.log(`Dialog message: ${dialog.message()}`);
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
    
    // Check for validation message. Matches loose text or exact content.
    // HTML is: <div ...> Name is required. </div>
    await expect(page.locator('text=Name is required')).toBeVisible();
    
    // Button should be disabled
    await expect(page.locator('button[type="submit"]')).toBeDisabled();
  });
});
