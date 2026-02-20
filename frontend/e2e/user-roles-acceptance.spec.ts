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
    // 1. Mockowanie zalogowanego Gracza
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({ json: { token: 'jwt', username: 'TestPlayer', role: 'PLAYER' } });
    });
    // Mockowanie listy postaci dla gracza
    await page.route('**/api/characters', async route => {
      await route.fulfill({ json: [{ id: 1, name: 'Bohater Testowy', characterClass: 'Warrior', level: 1 }] });
    });

    // 2. Logowanie
    await page.goto('/login');
    await page.fill('#username', 'TestPlayer');
    await page.fill('#password', 'password123');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja widoku Dashboardu Gracza (zgodnie z sekcją 4.2 PRD)
    await expect(page.locator('h1')).toHaveText('Player Dashboard');
    await expect(page.locator('.character-card h3')).toContainText('Bohater Testowy');
    
    // 4. Sprawdzenie braku uprawnień GM (Gracz nie powinien widzieć "Create New Campaign")
    await expect(page.locator('text=Create New Campaign')).not.toBeVisible();
  });
});

test.describe('Akceptacja: Mistrz Gry (GM)', () => {
  test('Powinien widzieć Dashboard GM i zarządzanie kampaniami', async ({ page }) => {
    // 1. Mockowanie zalogowanego GM
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({ json: { token: 'jwt', username: 'TestGM', role: 'GM' } });
    });
    await page.route('**/api/campaigns', async route => {
      await route.fulfill({ json: [{ id: 1, name: 'Kampania GM', description: 'Opis' }] });
    });

    // 2. Logowanie
    await page.goto('/login');
    await page.fill('#username', 'TestGM');
    await page.fill('#password', 'password123');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja widoku Dashboardu GM (zgodnie z sekcją 4.3 PRD)
    await expect(page.locator('h1')).toHaveText('GM Dashboard');
    await expect(page.locator('text=Create New Campaign')).toBeVisible();
  });
});

test.describe('Akceptacja: Administrator (ADMIN)', () => {
  test('Powinien widzieć opcje monitoringu i zarządzania (BETA - może wymagać implementacji)', async ({ page }) => {
    // 1. Mockowanie zalogowanego Admina
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({ json: { token: 'jwt', username: 'AdminUser', role: 'ADMIN' } });
    });

    // 2. Logowanie
    await page.goto('/login');
    await page.fill('#username', 'AdminUser');
    await page.fill('#password', 'password123');
    await page.click('button[type="submit"]');

    // 3. Weryfikacja sekcji ADMIN (sekcja 4.4 PRD)
    // Sprawdzamy czy nagłówek zawiera tekst Admin - test może zawieść jeśli widok nie istnieje
    const header = page.locator('h1');
    await expect(header).toBeVisible();
    await expect(header).toContainText('Admin');
  });
});
