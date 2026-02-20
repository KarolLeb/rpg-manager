# PRD - Role i Uprawnienia Użytkowników

## Dokument Wymagań Produktowych (PRD)

### 0. Historia zmian

| Data       | Autor      | Opis zmian         |
| ---------- | ---------- | ------------------ |
| 2026-02-20 | Gemini CLI | Pierwotna wersja   |

### 1. Przegląd

- **Problem:** Brak jasnego opisu ról i możliwości użytkowników w aplikacji RPG Manager, co utrudnia zrozumienie dostępnych funkcji dla poszczególnych typów kont.
- **Wartość:** Dokument ten stanowi źródło prawdy dla deweloperów i interesariuszy, definiując zakres działań każdego użytkownika.

### 2. Cele

**Cele strategiczne:**
- Zapewnienie spójności między backendem a frontendem w zakresie autoryzacji.
- Ułatwienie projektowania interfejsu użytkownika (UI) dostosowanego do ról.

**Cele mierzalne:**
- 100% pokrycia testami E2E dla kluczowych flow każdej roli.
- Implementacja kontroli dostępu (RBAC) na poziomie API dla wszystkich endpointów.

### 3. Interesariusze

| Grupa                  | Rola / Odpowiedzialność | Uwagi |
| ---------------------- | ----------------------- | ----- |
| Deweloperzy            | Implementacja logiki    |       |
| Mistrzowie Gry (GMs)   | Użytkownik docelowy     |       |
| Gracze (Players)       | Użytkownik docelowy     |       |

### 4. Specyfikacja i Typy Użytkowników

#### 4.1. Gość (Użytkownik Niezalogowany)
Użytkownik, który nie posiada jeszcze konta lub nie jest zalogowany.
- **Możliwości:**
  - Rejestracja nowego konta (domyślnie z rolą PLAYER).
  - Logowanie do systemu.
  - Resetowanie hasła (opcjonalnie).

#### 4.2. Gracz (PLAYER)
Podstawowy typ użytkownika systemu.
- **Możliwości:**
  - Zarządzanie kartami postaci (tworzenie, edycja, podgląd).
  - Przeglądanie dostępnych kampanii.
  - Dołączanie do kampanii za pomocą zaproszenia/kodu.
  - Rzucanie kośćmi (Dice Roller).
  - Podgląd własnego dashboardu z aktywnymi postaciami.

#### 4.3. Mistrz Gry (GM)
Użytkownik zarządzający rozgrywką. Posiada wszystkie uprawnienia Gracza oraz dodatkowo:
- **Możliwości:**
  - Tworzenie i zarządzanie kampaniami (edycja opisu, usuwanie).
  - Zarządzanie sesjami w ramach kampanii.
  - Zapraszanie graczy do kampanii.
  - Zarządzanie NPC i światem gry.
  - Dostęp do dashboardu GM z listą prowadzonych kampanii.

#### 4.4. Administrator (ADMIN)
Użytkownik odpowiedzialny za utrzymanie systemu.
- **Możliwości:**
  - Monitorowanie stanu aplikacji (Grafana, Prometheus).
  - Monitorowanie infrastruktury (Redis, PostgreSQL).
  - Zarządzanie użytkownikami (blokowanie, zmiana ról).
  - Dostęp do logów systemowych.

### 5. Wymagania Funkcjonalne

| ID   | Funkcjonalność                | Rola       | Priorytet |
| ---- | ----------------------------- |------------| --------- |
| F01  | Tworzenie kampanii            | ADMIN      | MUST      |
| F02  | Tworzenie karty postaci       | PLAYER, GM | MUST      |
| F03  | Dołączanie do kampanii        | PLAYER     | MUST      |
| F04  | Rzut kośćmi                   | Dowolna    | MUST      |
| F05  | Monitoring infrastruktury     | ADMIN      | SHOULD    |
| F06  | Edycja stylów ras (RaceStyle) | GM         | COULD     |
| F07  | Zarządzanie sesjami           | GM         | MUST      |

### 7. Wymagania Niefunkcjonalne
- **Bezpieczeństwo:** Każde żądanie musi być autoryzowane tokenem JWT.

### 13. Referencje i Powiązane Dokumenty
- [ADR: Hexagonal Architecture](../engineering/hexagonal-architecture.md)
- [Backend Instructions](../../.github/instructions/backend.instructions.md)
