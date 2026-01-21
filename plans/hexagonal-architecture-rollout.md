# Plan: Hexagonal Architecture Research & Implementation

## 2. Short description
Conduct in-depth research on Hexagonal Architecture (Ports and Adapters) best practices for Spring Boot and apply a standardized architectural pattern to the project's core modules to improve maintainability and testability.

## 3. Current status
```yaml
owner: AI Agent
state: proposed
last_updated: 2026-01-18
blockers: []
```

## 4. Objectives
1.  **Research:** Define a clear, project-specific standard for Hexagonal Architecture (package structure, naming conventions, boundary definitions).
2.  **Standardize:** Create architectural documentation to guide future development.
3.  **Implement:** Refactor the core modules (`Campaign`, `Character`) to strictly adhere to the defined standard, ensuring domain logic is decoupled from frameworks and persistence.

## 5. Success criteria
- name: `Domain Isolation`
  metric: `Dependency Check`
  target: `Domain packages have NO dependencies on Spring or JPA`
  verification: `ArchUnit tests or manual inspection`

- name: `Testability`
  metric: `Unit Test coverage`
  target: `Domain logic tested without mocks of external frameworks`
  verification: `JUnit execution`

## 6. Scope
**In Scope:**
- Researching Spring Boot Hexagonal patterns.
- Creating `docs/engineering/hexagonal-architecture.md`.
- Refactoring `Character` module (revisiting previous work).
- Refactoring `Campaign` module.

**Out of Scope:**
- changing the Database technology (Postgres stays).
- Frontend refactoring (Backend focus).

## 9. Task list
- T-001 | Research Hexagonal Architecture patterns for Spring Boot | AI Agent | complexity: S | done: true
- T-002 | Create Architecture Standard Documentation (`docs/engineering/hexagonal-architecture.md`) | AI Agent | complexity: S | deps: [T-001] | done: true
- T-003 | Refactor `Character` module to Ports & Adapters | AI Agent | complexity: L | deps: [T-002] | done: true
- T-004 | Refactor `Campaign` module to Ports & Adapters | AI Agent | complexity: L | deps: [T-002] | done: false
- T-005 | Verify implementations with ArchUnit tests | AI Agent | complexity: M | deps: [T-003, T-004] | done: false

## 12. Implementation approach
We will adopt a package structure similar to:
- `domain`: Pure business logic and models.
- `application`: Use cases, ports (interfaces).
- `infrastructure`: Adapters (Web Controllers, JPA Repositories).
