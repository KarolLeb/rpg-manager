import { test, expect } from './coverage.fixture';

test.describe('API Direct Integration Tests (Backend Coverage Boost)', () => {

  test('should roll a die via API', async ({ request, page }) => {
    // 1. Zaloguj się, aby uzyskać token
    await page.goto('/login');
    await page.fill('#username', 'player1');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/dashboard');

    const token = await page.evaluate(() => localStorage.getItem('token'));
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
    
    const response = await request.get('/api/dice/roll/20', { headers });
    expect(response.ok()).toBeTruthy();
    const result = await response.json();
    expect(typeof result).toBe('number');
    expect(result).toBeGreaterThanOrEqual(1);
    expect(result).toBeLessThanOrEqual(20);
  });

  test('should manage activity logs via API', async ({ request, page }) => {
    // 1. Logowanie jako GM
    await page.goto('/login');
    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/dashboard');

    const token = await page.evaluate(() => localStorage.getItem('token'));
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

    // 2. Pobierz ID kampanii
    const campaignsRes = await request.get('/api/campaigns', { headers });
    expect(campaignsRes.ok()).toBeTruthy();
    const campaigns = await campaignsRes.json();
    const campaignId = campaigns[0].id;

    // 3. Pobierz logi dla tej kampanii
    const logsRes = await request.get(`/api/activity-log/campaign/${campaignId}`, { headers });
    expect(logsRes.ok()).toBeTruthy();
    const logs = await logsRes.json();
    expect(Array.isArray(logs)).toBeTruthy();

    // 4. Testuj wyszukiwanie semantyczne
    const searchRes = await request.get('/api/activity-log/search?q=smoczej&limit=5', { headers });
    expect(searchRes.ok()).toBeTruthy();
    const searchResults = await searchRes.json();
    expect(Array.isArray(searchResults)).toBeTruthy();
  });

  test('should check admin health via API', async ({ request, page }) => {
     // 1. Logowanie jako Admin
     await page.goto('/login');
     await page.fill('#username', 'admin');
     await page.fill('#password', 'password');
     await page.click('button[type="submit"]');
     await expect(page).toHaveURL('/dashboard');
 
     const token = await page.evaluate(() => localStorage.getItem('token'));
     const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

     const response = await request.get('/api/admin/health', { headers });
     expect(response.ok()).toBeTruthy();
     const text = await response.text();
     expect(text).toBe('Admin module is healthy');

     // Test user management API in backend-admin
     const usersRes = await request.get('/api/admin/users', { headers });
     expect(usersRes.ok()).toBeTruthy();
     const users = await usersRes.json();
     expect(Array.isArray(users)).toBeTruthy();
     expect(users.length).toBeGreaterThan(0);
  });

  test('should allow character to join campaign via API', async ({ request, page }) => {
    // 1. Logowanie jako player1
    await page.goto('/login');
    await page.fill('#username', 'player1');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/dashboard');

    const token = await page.evaluate(() => localStorage.getItem('token'));
    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

    // 2. Pobierz ID postaci Geralta
    const charactersRes = await request.get('/api/characters', { headers });
    expect(charactersRes.ok()).toBeTruthy();
    const characters = await charactersRes.json();
    const geralt = characters.find((c: any) => c.name.includes('Geralt'));
    expect(geralt).toBeDefined();

    // 3. Dołącz do kampanii
    const joinRes = await request.post(`/api/characters/${geralt.id}/join-campaign/1`, { headers });
    expect(joinRes.ok()).toBeTruthy();
    const updatedGeralt = await joinRes.json();
    expect(updatedGeralt.campaignName).toBe('Kampania Smoczej Lancy');
  });
});
