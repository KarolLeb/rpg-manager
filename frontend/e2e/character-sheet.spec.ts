import { test, expect } from '@playwright/test';

test.describe('Character Sheet Feature', () => {
  const mockCharacter = {
    id: 1,
    name: 'Test Character',
    characterClass: 'Tester',
    level: 1,
    stats: JSON.stringify({
      strength: { val: 10, skills: [['Skill1', 1, 11]] },
      constitution: { val: 10, skills: [] },
      dexterity: { val: 10, skills: [] },
      agility: { val: 10, skills: [] },
      perception: { val: 10, skills: [] },
      empathy: { val: 10, skills: [] },
      charisma: { val: 10, skills: [] },
      intelligence: { val: 10, skills: [] },
      knowledge: { val: 10, skills: [] },
      willpower: { val: 10, skills: [] }
    })
  };

  test.beforeEach(async ({ page }) => {
    // Mock Authentication
    await page.addInitScript(() => {
      window.localStorage.setItem('token', 'fake-jwt-token');
      window.localStorage.setItem('currentUser', JSON.stringify({ username: 'TestGM', role: 'GM' }));
    });

    // Mock the GET request to return our mock character
    await page.route('**/api/characters/1', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockCharacter)
      });
    });
  });

  test('should load character data', async ({ page }) => {
    await page.goto('/character/1');

    // Check if the name field is populated
    const nameInput = page.locator('input[formControlName="name"]');
    await expect(nameInput).toHaveValue('Test Character');

    // Check if the profession field is populated
    const professionInput = page.locator('input[formControlName="profession"]');
    await expect(professionInput).toHaveValue('Tester');
    
    // Check if attributes are loaded
    await expect(page.getByText('SIŁA', { exact: true })).toBeVisible();
  });

  test('should update and save character data', async ({ page }) => {
    await page.goto('/character/1');

    // Mock the PUT request
    let savedData: any;
    await page.route('**/api/characters/1', async route => {
        savedData = JSON.parse(route.request().postData() || '{}');
        await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(savedData)
        });
    });
    
    // Change Name
    const nameInput = page.locator('input[formControlName="name"]');
    await nameInput.fill('Updated Name');

        // Click Save
        const saveButton = page.locator('.save-btn', { hasText: 'ZAPISZ' });
        await expect(saveButton).toBeVisible();
    
        // Set up promises before the action that triggers them
        const responsePromise = page.waitForResponse(response => 
            response.url().includes('/api/characters/1') && response.request().method() === 'PUT',
            { timeout: 10000 }
        );
    
        await saveButton.click();
    
        // Wait for response
        await responsePromise;
    
        // Wait for Toast success message
        const toast = page.locator('app-toast .toast-item.success');
        await expect(toast).toBeVisible();
        await expect(toast).toContainText('Character saved successfully');
    
        expect(savedData.name).toBe('Updated Name');
      });
    });
    