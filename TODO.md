# RPG Manager - Plan Zadań

## Backend (Spring Boot) - Struktura Danych i API

### Zarządzanie Sesjami (Session)
- [ ] **Encja `Session`**:
    - Pola: `id`, `name`, `description`, `creationDate`, `status` (np. ACTIVE, FINISHED), `gameMasterId`.
- [ ] **Encja `User`** (Wstępna implementacja pod relacje):
    - Pola: `id`, `username`, `role` (GM/PLAYER).
- [ ] **Aktualizacja Encji `Character`**:
    - Dodać relację do `Session` (Many-to-One) - każda postać musi być w sesji.
    - Dodać relację do `User` (Many-to-One) - postać należy do gracza.
    - Dodać flagę/enum `type` (np. PERMANENT, TEMPORARY) - rozróżnienie postaci stałych i tymczasowych.
- [ ] **Migracje Flyway**:
    - `V3__create_session_and_user_tables.sql`: Nowe tabele.
    - `V4__update_character_relations.sql`: Dodanie kolumn `session_id`, `user_id`, `character_type` do tabeli `character`.
- [ ] **Logika Biznesowa (Service/Controller)**:
    - Endpoint do listowania wszystkich sesji (dla GM).
    - Endpoint do listowania postaci danego użytkownika (historia postaci).
    - Logika przypisywania postaci do sesji.

## Frontend (Angular) - Interfejs Użytkownika

### Modele i Serwisy
- [ ] Stworzyć modele TypeScript: `Session`, `User`.
- [ ] Zaktualizować model `Character` (relacja z sesją).
- [ ] Stworzyć `SessionService` (pobieranie listy sesji).

### Widoki (Views)
- [ ] **Dashboard Wyboru Sesji (Dla GM)**:
    - Lista aktywnych i zakończonych sesji.
    - Przycisk "Wybierz" lub "Utwórz nową".
- [ ] **Ekran Wyboru Postaci (Dla Gracza)**:
    - Widok "Moje Postacie" - lista postaci użytkownika.
    - Filtrowanie po sesjach (obecna vs archiwalne).
    - Oznaczenie postaci "Tymczasowych".

## Ogólne / Infrastruktura
- [ ] Upewnić się, że Docker Compose obsługuje nowe zmiany w bazie danych.
