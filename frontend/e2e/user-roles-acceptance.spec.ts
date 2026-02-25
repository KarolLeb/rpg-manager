import { test, expect } from '@playwright/test';

/**
 * TESTY AKCEPTACYJNE - PRD: Role i Uprawnienia Użytkowników
 * Mapowanie sekcji 4.1 - 4.4 z dokumentu PRD na zachowanie UI.
 */

test.describe('Akceptacja: Gość (Użytkownik Niezalogowany)', () => {
  test('Powinien mieć możliwość wejścia na stronę logowania i rejestracji', async ({ page }) => {
    // 1. Przejdź na stronę logowania
    await page.goto('/login');
    await expect(page.locator('h2')).toContainText('Login');

    // 2. Przejdź do rejestracji
    await page.click('text=Register here');
    await expect(page).toHaveURL(/\/register/);
  });
});

test.describe('Akceptacja: Gracz (PLAYER)', () => {
  test('Powinien widzieć Dashboard Gracza i swoje postacie', async ({ page }) => {
    // 2. Logowanie
    await page.goto('/login');
    await page.fill('#username', 'player1');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja widoku Dashboardu Gracza (zgodnie z sekcją 4.2 PRD)
    await expect(page.locator('h1')).toHaveText('Player Dashboard');
    // Note: Geralt is a seeded character for player1 in V2__insert_test_data.sql
    await expect(page.locator('.character-card', { hasText: 'Geralt' })).toBeVisible();

    // 4. Sprawdzenie braku uprawnień GM (Gracz nie powinien widzieć "Create New Campaign")
    await expect(page.locator('text=Create New Campaign')).not.toBeVisible();
  });

  test('Nie powinien mieć dostępu do formularza tworzenia kampanii', async ({ page }) => {
    // 1. Logowanie jako Gracz
    await page.goto('/login');
    await page.fill('#username', 'player1');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(/\/dashboard/);

    // 2. Próba bezpośredniego wejścia na URL dla GM
    await page.goto('/campaigns/new');

    // 3. Powinien zostać przekierowany na dashboard (bo jest zalogowany, ale ma złą rolę)
    await expect(page).toHaveURL(/\/dashboard/);

    // 4. Powinien zobaczyć Toast z błędem
    const toast = page.locator('app-toast .toast-item.error');
    await expect(toast).toBeVisible();
    await expect(toast).toContainText('Access Denied');
  });
});

test.describe('Zabezpieczenia: Gość (Użytkownik Niezalogowany)', () => {
  test('Nie powinien mieć dostępu do Dashboardu bez logowania', async ({ page }) => {
    // 1. Próba wejścia na chroniony zasób
    await page.goto('/dashboard');

    // 2. Powinien zostać przekierowany na /login (AuthGuard)
    await expect(page).toHaveURL(/\/login/);
  });
});

test.describe('Akceptacja: Mistrz Gry (GM)', () => {
  test('Powinien widzieć Dashboard GM i zarządzanie kampaniami', async ({ page }) => {
    // 1. Logowanie jako GM
    await page.goto('/login');
    await page.fill('#username', 'gamemaster');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja widoku Dashboardu GM
    await expect(page.locator('h1')).toHaveText('GM Dashboard');
    // Note: Kampania Smoczej Lancy is seeded for gamemaster
    await expect(page.locator('.campaign-card', { hasText: 'Kampania Smoczej Lancy' })).toBeVisible();
    await expect(page.locator('text=Create New Campaign')).toBeVisible();
  });
});

test.describe('Akceptacja: Administrator (ADMIN)', () => {
  test('Powinien widzieć opcje monitoringu i zarządzania (BETA - może wymagać implementacji)', async ({ page }) => {
    // 2. Logowanie jako Admin
    await page.goto('/login');
    await page.fill('#username', 'admin');
    await page.fill('#password', 'password');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja sekcji ADMIN (sekcja 4.4 PRD)
    // Sprawdzamy czy nagłówek zawiera tekst Admin - test może zawieść jeśli widok nie istnieje
    const header = page.locator('h1');
    await expect(header).toBeVisible();
    await expect(header).toContainText('Admin');
  });
});
