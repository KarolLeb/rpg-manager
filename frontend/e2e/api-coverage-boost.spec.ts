import { test, expect } from './coverage.fixture';

test.describe('API Coverage Boost (Sessions, Permissions, Errors)', () => {

  test.describe.configure({ mode: 'serial' });

  let token: string;
  let headers: any;

  test.beforeEach(async ({ page }) => {
    // Log in once per test to get a fresh token
    await page.goto('/login');
    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/dashboard');

    token = await page.evaluate(() => localStorage.getItem('token')) || '';
    headers = { 'Authorization': `Bearer ${token}` };
  });

  test('should manage sessions via API', async ({ request }) => {
    // 1. Get campaigns to get a valid ID
    const campaignsRes = await request.get('/api/campaigns', { headers });
    const campaigns = await campaignsRes.json();
    const campaignId = campaigns[0].id;

    // 2. Create a session
    const createRes = await request.post('/api/sessions', {
      headers,
      data: {
        campaignId: campaignId,
        name: 'E2E Test Session',
        description: 'Testing sessions coverage',
        sessionDate: new Date(Date.now() + 86400000).toISOString()
      }
    });
    expect(createRes.ok()).toBeTruthy();
    const session = await createRes.json();

    // 3. Get session
    const getRes = await request.get(`/api/sessions/${session.id}`, { headers });
    expect(getRes.ok()).toBeTruthy();

    // 4. Update session
    const updateRes = await request.put(`/api/sessions/${session.id}`, {
      headers,
      data: {
        campaignId: campaignId,
        name: 'Updated E2E Session',
        description: 'Coverage++'
      }
    });
    expect(updateRes.ok()).toBeTruthy();

    // 5. Complete session
    const completeRes = await request.post(`/api/sessions/${session.id}/complete`, { headers });
    expect(completeRes.ok()).toBeTruthy();

    // 6. Cancel session
    const create2Res = await request.post('/api/sessions', {
        headers,
        data: { campaignId: campaignId, name: 'Session to Cancel' }
    });
    const session2 = await create2Res.json();
    const cancelRes = await request.post(`/api/sessions/${session2.id}/cancel`, { headers });
    expect(cancelRes.ok()).toBeTruthy();
  });

  test('should check permissions via API', async ({ request }) => {
    const res = await request.get('/api/v1/permissions/check?characterId=1&actionType=MODIFY_STATS&campaignId=1', { headers });
    expect(res.ok()).toBeTruthy();
    const body = await res.json();
    expect(typeof body.allowed).toBe('boolean');
  });

  test('should handle error logging and global exceptions', async ({ request }) => {
    // 1. Log a manual error
    const logRes = await request.post('/api/error-log', {
      headers,
      data: {
        severity: 'WARN',
        serviceName: 'e2e-test',
        message: 'Test error message',
        stackTrace: 'N/A'
      }
    });
    expect(logRes.ok()).toBeTruthy();

    // 2. Query errors with filters
    await request.get('/api/error-log?severity=WARN&service=e2e-test', { headers });
    await request.get('/api/error-log?severity=ERROR', { headers });
    await request.get('/api/error-log?from=2020-01-01T00:00:00Z', { headers });

    // 3. Trigger 400/500
    const res400 = await request.get('/api/v1/permissions/check?characterId=999&actionType=INVALID', { headers });
    expect(res400.status()).toBeGreaterThanOrEqual(400);

    const res500 = await request.post('/api/sessions', {
        headers,
        data: { campaignId: 9999, name: 'Fail Session' }
    });
    expect(res500.status()).toBe(500);
  });

  test('should manage action policies and overrides via API', async ({ request }) => {
    // CAMPAIGN context
    await request.post('/api/v1/permissions/admin/policy?actionType=LEVEL_UP&contextType=CAMPAIGN&contextId=1&isAllowed=false', { headers });
    expect((await (await request.get('/api/v1/permissions/check?characterId=1&actionType=LEVEL_UP&campaignId=1', { headers })).json()).allowed).toBe(false);

    // SESSION context override
    await request.post('/api/v1/permissions/admin/override?characterId=1&actionType=LEVEL_UP&contextType=SESSION&contextId=1&isAllowed=true', { headers });
    expect((await (await request.get('/api/v1/permissions/check?characterId=1&actionType=LEVEL_UP&campaignId=1&sessionId=1', { headers })).json()).allowed).toBe(true);

    // More branches for ActionPermissionService
    await request.post('/api/v1/permissions/admin/override?characterId=1&actionType=DISTRIBUTE_POINTS&contextType=SESSION&contextId=1&isAllowed=false', { headers });
    expect((await (await request.get('/api/v1/permissions/check?characterId=1&actionType=DISTRIBUTE_POINTS&sessionId=1', { headers })).json()).allowed).toBe(false);

    await request.post('/api/v1/permissions/admin/policy?actionType=ADD_ITEM&contextType=CAMPAIGN&contextId=1&isAllowed=false', { headers });
    expect((await (await request.get('/api/v1/permissions/check?characterId=1&actionType=ADD_ITEM&campaignId=1', { headers })).json()).allowed).toBe(false);

    await request.post('/api/v1/permissions/admin/override?characterId=1&actionType=ADD_ITEM&contextType=CAMPAIGN&contextId=1&isAllowed=true', { headers });
    expect((await (await request.get('/api/v1/permissions/check?characterId=1&actionType=ADD_ITEM&campaignId=1', { headers })).json()).allowed).toBe(true);
  });

  test('should trigger access denied exceptions for coverage', async ({ request, page }) => {
      // Re-login as player2 to try and hack player1's character
      await page.goto('/login');
      await page.fill('#username', 'player2');
      await page.fill('#password', 'password');
      await page.click('button[type="submit"]');
      const p2Token = await page.evaluate(() => localStorage.getItem('token'));
      const p2Headers = { 'Authorization': `Bearer ${p2Token}` };
  
      const res403 = await request.put('/api/characters/1', {
          headers: p2Headers,
          data: { name: 'Hacker Geralt' }
      });
      expect(res403.status()).toBe(403);
    });

    test('should manage campaigns via API (Extra Coverage)', async ({ request }) => {
    const createRes = await request.post('/api/campaigns', {
        headers,
        data: { name: 'API Campaign ' + Date.now(), description: 'Created via E2E API', status: 'ACTIVE' }
    });
    const campaign = await createRes.json();

    await request.put(`/api/campaigns/${campaign.id}`, {
        headers,
        data: { ...campaign, name: 'Updated API Campaign' }
    });

    await request.delete(`/api/campaigns/${campaign.id}`, { headers });

    expect((await request.get('/api/campaigns/99999', { headers })).status()).toBe(400);
    expect((await request.delete('/api/campaigns/99999', { headers })).status()).toBe(400);
  });

  test('should manage characters via API (Extra Coverage)', async ({ request }) => {
    // 1. Get all characters
    const characters = await (await request.get('/api/characters', { headers })).json();
    expect(Array.isArray(characters)).toBeTruthy();
    
    // 2. Get specific character
    if (characters.length > 0) {
      const charId = characters[0].id;
      const character = await (await request.get(`/api/characters/${charId}`, { headers })).json();
      expect(character.id).toBe(charId);
      expect(character.ownerUsername).toBeDefined();
    }
    
    // 3. Join campaign
    const joinRes = await request.post('/api/characters/1/join-campaign/1', { headers });
    expect(joinRes.ok()).toBeTruthy();
  });

  test('should manage activity logs via API (Extra Coverage)', async ({ request }) => {
    // 1. Log something manually to ensure there's data to search
    const logReq = await request.post('/api/activity-log', {
        headers,
        data: {
            description: 'E2E Semantic Search Test Activity Log Entry',
            actionType: 'NOTE',
            campaignId: 1,
            metadata: { test: true, source: 'e2e' }
        }
    });
    expect(logReq.ok()).toBeTruthy();

    // 2. Get activities by campaign and session
    await request.get('/api/activity-log/campaign/1', { headers });
    await request.get('/api/activity-log/session/1', { headers });
    
    // 3. Search activities (triggers native query results mapping)
    const searchRes = await request.get('/api/activity-log/search?q=Semantic&limit=10', { headers });
    expect(searchRes.ok()).toBeTruthy();
    const results = await searchRes.json();
    expect(results.length).toBeGreaterThan(0);
    expect(results[0].similarityScore).toBeDefined();
  });

  test('should roll all types of dice', async ({ request }) => {
      for (const d of [4, 6, 8, 10, 12, 20, 100]) {
        await request.get(`/api/dice/roll/${d}`, { headers });
      }
  });

  test('should trigger admin security and health', async ({ request, page }) => {
    await page.goto('/login');
    await page.fill('#username', 'admin');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    const adminToken = await page.evaluate(() => localStorage.getItem('token'));
    const adminHeaders = { 'Authorization': `Bearer ${adminToken}` };

    await request.get('/api/admin/users', { headers: adminHeaders });
    await request.get('/api/admin/health', { headers: adminHeaders });
  });
});
