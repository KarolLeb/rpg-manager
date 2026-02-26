import { test, expect } from './coverage.fixture';

test('has title', async ({ page }) => {
  await page.goto('/');

  // Expect a title "to contain" a substring.
  await expect(page).toHaveTitle(/RpgClient/);
});
