import { test, expect } from './coverage.fixture';

test.describe('Dynamic CSS Integration', () => {

    test.beforeEach(async ({ page }) => {
        // 1. Logowanie jako Gracz
        await page.goto('/login');
        await page.fill('#username', 'player1');
        await page.fill('#password', 'password');
        await page.click('button[type="submit"]');
        await expect(page).toHaveURL('/dashboard');
    });

    test('should apply aggregated dynamic styles to character sheet', async ({ page, request }) => {
        const characterId = 1; // Geralt

        // 0. Login to get token for API setup
        const authResponse = await request.post('http://localhost:8081/api/auth/login', {
            data: { username: 'player1', password: 'password' }
        });
        const { token } = await authResponse.json();
        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'text/plain'
        };

        // 1. Setup dynamic styles via API
        const campaignId = 1;

        // Reset/Setup Global style
        await request.post(`http://localhost:8080/api/styles/DEFAULT/global`, {
            data: ':root { --test-global-style: "global-works"; }',
            headers
        });

        // Setup Campaign specific style
        await request.post(`http://localhost:8080/api/styles/CAMPAIGN/${campaignId}`, {
            data: ':root { --test-campaign-style: "campaign-works"; }',
            headers
        });

        // Setup Campaign:Race style
        await request.post(`http://localhost:8080/api/styles/CAMPAIGN/${campaignId}:Witcher`, {
            data: ':root { --test-race-style: "witcher-works"; }',
            headers
        });

        // Setup Character specific style
        await request.post(`http://localhost:8080/api/styles/CHARACTER/${characterId}`, {
            data: ':root { --test-character-style: "geralt-works"; --character-primary-color: rgb(255, 0, 255); }',
            headers
        });


        // 2. Navigate to Geralt's sheet
        await page.goto('/dashboard');

        // Setup listeners for debugging
        page.on('console', msg => console.log('BROWSER LOG:', msg.text()));
        page.on('requestfailed', request => console.log('REQUEST FAILED:', request.url(), request.failure()?.errorText));

        const responsePromise = page.waitForResponse(response =>
            response.url().includes('/api/styles/aggregated') && response.status() === 200,
            { timeout: 15000 }
        );

        await page.locator('.character-card', { hasText: 'Geralt' }).getByRole('link', { name: 'View Sheet' }).click();
        await expect(page).toHaveURL(new RegExp(`/character/${characterId}`));

        // Wait for the style aggregation response to finish
        await responsePromise;

        // Give a tiny bit for the renderer to apply
        await page.waitForTimeout(500);

        // 3. Verify the <style> element exists and has content
        const styleTag = page.locator('head style#dynamic-character-styles');
        await expect(styleTag).toBeAttached();
        const styleContent = await styleTag.innerHTML();
        console.log('DYNAMIC CSS CONTENT:', styleContent);

        // 4. Verify CSS variables are applied to the document/root
        const container = page.locator('.sheet-container');
        await expect(container).toBeVisible();

        // Check variables using evaluate
        const styles = await container.evaluate((el) => {
            const computed = getComputedStyle(el);
            return {
                global: computed.getPropertyValue('--test-global-style').trim().replace(/"/g, ''),
                campaign: computed.getPropertyValue('--test-campaign-style').trim().replace(/"/g, ''),
                race: computed.getPropertyValue('--test-race-style').trim().replace(/"/g, ''),
                character: computed.getPropertyValue('--test-character-style').trim().replace(/"/g, ''),
                color: computed.getPropertyValue('--character-primary-color').trim()
            };
        });

        console.log('COMPUTED STYLES:', styles);

        expect(styles.global).toBe('global-works');
        expect(styles.campaign).toBe('campaign-works');
        expect(styles.race).toBe('witcher-works');
        expect(styles.character).toBe('geralt-works');
        expect(styles.color).toBe('rgb(255, 0, 255)');


    });
});
