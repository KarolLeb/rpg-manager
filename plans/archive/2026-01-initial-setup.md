# Plan: Initial Project Setup & Core Entities (Completed)

## 2. Short description

Initial setup of the Spring Boot backend, Postgres database, and core JPA entities including User, Campaign, Session, and Character.

## 3. Current status

```yaml
owner: AI Agent <ai@rpgmanager.com>
state: complete
last_updated: 2026-01-18
blockers: []
```

## 4. Objectives

1. Initialize project structure.
2. Define core data models.
3. Secure basic authentication infrastructure.

## 5. Success criteria

- name: `Database Schema`
  metric: `Flyway Migration Success`
  target: `V1__init_schema.sql applied successfully`
  verification: `Application startup logs`

## 6. Scope

in:
- Backend `pom.xml` dependencies (Security, JWT, Web, JPA).
- Entities: `User`, `Campaign`, `Session`, `Character`.
- Basic Security Config (JWT Util, Auth Controller).
- Flyway Migrations V1 & V2.
- Playwright Setup.

## 7. Stakeholders & Roles

- AI Agent — Lead Developer — responsible for implementation — ai@rpgmanager.com

## 8. High-level timeline & milestones

1. M1 — Project Init — 2026-01-10 — AI Agent
2. M2 — Entities & Auth — 2026-01-15 — AI Agent

## 9. Task list

- T-001 | Configure `pom.xml` dependencies | AI Agent | complexity: S | deps: [] | done: true
- T-002 | Create `User` Entity | AI Agent | complexity: S | deps: [] | done: true
- T-003 | Implement JWT Auth Logic (`SecurityConfig`, `JwtUtil`) | AI Agent | complexity: M | deps: [T-002] | done: true
- T-004 | Create `Campaign`, `Session`, `Character` Entities | AI Agent | complexity: M | deps: [] | done: true
- T-005 | Create Flyway Migration `V1__init_schema.sql` | AI Agent | complexity: M | deps: [T-004] | done: true
- T-006 | Setup Playwright & Campaign E2E Test | AI Agent | complexity: M | deps: [] | done: true

## 10. Risks and mitigations

- N/A

## 11. Assumptions

- N/A

## 12. Implementation approach

Spring Boot 3.2+, Java 17, PostgreSQL 16.

## 13. Testing & validation plan

- `mvn clean install` passes.
- `docker-compose up` starts DB and App connects successfully.

## 14. Deployment plan & roll-back strategy

- N/A

## 15. Monitoring & observability

- N/A

## 16. Compliance, security & privacy considerations

- N/A

## 17. Communication plan

- N/A

## 18. Related documents & links

- N/A

## 19. Appendix

- N/A
