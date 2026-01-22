# Plan: User Module Hexagonal Refactoring

## 2. Short description
Refactor the legacy `User` module to adhere to the Hexagonal Architecture (Ports & Adapters) standard. This is critical to resolve dependency violations in other modules (like `Campaign`) that currently depend on the Infrastructure-layer `UserRepository` (JPA).

## 3. Current status
```yaml
owner: AI Agent
state: completed
last_updated: 2026-01-22
blockers: []
```

## 9. Task list
- T-001 | Create `UserDomain` model (POJO) | AI Agent | complexity: S | done: true
- T-002 | Define `UserRepositoryPort` interface in `user.domain.repository` | AI Agent | complexity: XS | done: true
- T-003 | Move existing `User` entity to `user.infrastructure.adapter.outgoing.persist` and rename to `UserEntity` | AI Agent | complexity: S | done: true
- T-004 | Create `UserPersistenceMapper` (MapStruct) | AI Agent | complexity: S | done: true
- T-005 | Implement `UserPersistenceAdapter` implementing `UserRepositoryPort` | AI Agent | complexity: M | done: true
- T-006 | Refactor `AuthService` to use `UserRepositoryPort` | AI Agent | complexity: L | done: true
- T-007 | Update `CampaignApplicationService` to use `UserRepositoryPort` | AI Agent | complexity: S | done: true
- T-008 | Update `CharacterApplicationService` to use `UserRepositoryPort` | AI Agent | complexity: S | done: true
- T-009 | Fix all tests | AI Agent | complexity: M | done: true


## 12. Implementation approach
We will follow the same pattern used for `Campaign` and `Character` modules.
1.  **Structure:** `domain`, `application`, `infrastructure` packages.
2.  **Step-by-step:**
    *   Duplicate `User` to `UserEntity` and `UserDomain`.
    *   Create Port.
    *   Implement Adapter.
    *   Switch consumers one by one.
