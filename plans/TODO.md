# RPG Manager - Plan Zadań

## Backend (Spring Boot) - Struktura Danych i API

### Bezpieczeństwo i Użytkownicy (JWT)
- [x] **Konfiguracja i Zależności**:
    - [x] Dodać `spring-boot-starter-security` oraz biblioteki JWT (`jjwt`) do `pom.xml`.
- [x] **Encja `User`**:
    - [x] Pola: `id`, `uuid`, `username`, `password` (hash), `role` (GM/PLAYER), `email`.
- [x] **Logika Logowania**:
    - [x] `SecurityConfig`, `UserDetailsService`, `JwtUtil`, `AuthController`.
- [x] **Migracje Flyway**:
    - [x] `V1__init_schema.sql`: Tabela użytkowników, kampanii, sesji i postaci (z UUID).

### Zarządzanie Kampaniami i Sesjami
- [x] **Encja `Campaign`**:
    - [x] Pola: `uuid`, `name`, `description`, `gameMaster`.
- [x] **Encja `Session`**:
    - [x] Pola: `uuid`, `campaign`, `name`, `date`, `status`.
- [x] **Encja `Character`**:
    - [x] Relacje: `campaign`, `user` (owner), `controller` (temporary).
- [x] **Logika Biznesowa (Service/Controller)**:
    - [ ] `CampaignService`: CRUD dla kampanii (dla GM).
    - [ ] `SessionService`: CRUD dla sesji w ramach kampanii.
    - [ ] `CharacterService`: Tworzenie postaci w kampanii, przypisywanie kontrolera.
- [x] **Testy E2E (Playwright)**:
    - [x] Implementacja scenariusza Campaign CRUD.

## Frontend (Angular) - Interfejs Użytkownika

### Autoryzacja
- [ ] **Logowanie**:
    - Widok logowania (`LoginComponent`).
    - `AuthService` (JWT storage).
    - `AuthGuard`.

### Widoki (Views)
- [ ] **Dashboard GM**:
    - Lista Kampanii.
    - Widok Kampanii: Lista Sesji + Lista Postaci.
- [ ] **Dashboard Gracza**:
    - "Moje Kampanie".
    - Wybór postaci (lub podgląd przypisanej).

## Jakość i Pokrycie Testami (Quality Gate)
- [ ] **Konfiguracja Raportowania**:
    - [ ] Skonfigurować JaCoCo dla Backend (Maven).
    - [ ] Skonfigurować Karma/Istanbul coverage reporter dla Frontend.
- [ ] **Osiągnięcie Targetów Pokrycia**:
    - [ ] Globalne pokrycie ≥ 90%.
    - [ ] Moduły core (logika biznesowa) ≥ 95%.
    - [ ] Ścieżki krytyczne (security, error handling) 100%.
- [ ] **Automatyzacja**:
    - [ ] Zintegrować `scripts/enforce-coverage.js` z procesem budowania.

## Ogólne
- [ ] Reset bazy danych (`docker-compose down -v`) po zmianie modelu.