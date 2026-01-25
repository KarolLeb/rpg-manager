# Analysis Report: Microservices Extraction (Auth & Admin)

## Executive Summary
The code analysis confirms that the `User` module has been partially decoupled via Hexagonal Architecture (Ports & Adapters), but significant **persistence-layer coupling** remains. The `Campaign` and `Character` domains are tightly coupled to the `User` domain via Database Foreign Keys and direct usage of `JpaUserRepository`.

## 1. Coupling Analysis

### A. Code Coupling (Application Layer) ✅
*   **Status:** Healthy.
*   **Details:** `CampaignApplicationService`, `AuthService`, and `AdminController` correctly use the `UserRepositoryPort` interface. They do not depend on the implementation details.
*   **Implication:** When we extract the Auth service, we can simply provide a new implementation of `UserRepositoryPort` (e.g., `UserRemoteAdapter`) that makes HTTP calls, without changing the application logic.

### B. Persistence Coupling (Infrastructure Layer) ❌
*   **Status:** Critical Coupling.
*   **Details:**
    *   `CampaignPersistenceAdapter` and `CharacterPersistenceAdapter` directly import `UserEntity` and `JpaUserRepository`.
    *   **Files:**
        *   `backend/src/main/java/com/rpgmanager/backend/campaign/infrastructure/adapter/outgoing/persist/CampaignPersistenceAdapter.java`
        *   `backend/src/main/java/com/rpgmanager/backend/character/infrastructure/adapter/outgoing/persistence/CharacterPersistenceAdapter.java`
*   **Implication:** These adapters cannot function if the `UserEntity` class and `JpaUserRepository` are moved to a different service.

### C. Database Coupling ❌
*   **Status:** Critical Coupling.
*   **Details:**
    *   `CampaignEntity` has `@ManyToOne` `gameMaster` (UserEntity).
    *   `CharacterEntity` has `@ManyToOne` `user` and `controller` (UserEntity).
*   **Implication:** The `campaigns` and `characters` tables have physical foreign keys to the `users` table. This prevents splitting the database.

## 2. Refactoring Strategy

Before physical extraction, we must perform a **Logical Decoupling** within the monolith:

1.  **Replace Relations with IDs:**
    *   Modify `CampaignEntity` to store `Long gameMasterId` instead of `UserEntity gameMaster`.
    *   Modify `CharacterEntity` to store `Long userId` instead of `UserEntity user`.
2.  **Update Persistence Adapters:**
    *   Remove `JpaUserRepository` dependency from `CampaignPersistenceAdapter` and `CharacterPersistenceAdapter`.
    *   When saving: Just save the ID.
    *   When loading: If the Domain object needs full User data, the Adapter must use `UserRepositoryPort.findById()` (which allows us to swap the implementation later).

## 3. Revised Roadmap

1.  **Refactor Persistence:** Break foreign keys and use ID references (Task T-001.5).
2.  **Scaffold Auth Service:** Create the new project.
3.  **Migrate Logic:** Move Auth/User logic to the new service.
4.  **Implement Remote Adapter:** Create `UserRemoteAdapter` in Core to call Auth Service.
