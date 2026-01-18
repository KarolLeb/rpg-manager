# Plan: Architecture Refactor for Scalability & Modernization

## 2. Short description

Refactoring the backend to meet modern enterprise standards (Hexagonal Architecture, Microservices readiness) and optimizing performance with caching, aligning with recruitment requirements.

## 3. Current status

```yaml
owner: AI Agent <ai@rpgmanager.com>
state: in-progress
last_updated: 2026-01-18
blockers: []
```

## 4. Objectives

1. Decouple domain logic from persistence framework (Hexagonal Architecture) in the `Character` module.
2. Optimize system performance by caching static/semi-static data (Race Styles) using Redis.
3. Demonstrate distributed system integration capabilities using Spring Cloud Feign for an external Dice Roller service.
4. Prepare the monolith for decomposition by isolating `Auth` and `Admin` domains.

## 5. Success criteria

- name: `Hexagonal Decoupling`
  metric: `Service dependency on javax.persistence`
  target: `0 direct dependencies in domain classes`
  verification: `Code review and package dependency analysis`

- name: `Cache Hit Rate`
  metric: `Redis keys presence`
  target: `Styles found in Redis after first fetch`
  verification: `Redis CLI check keys *`

- name: `Resilience`
  metric: `Dice Roll Success`
  target: `100% (fallback to local if external API fails)`
  verification: `Integration test with mocked external failure`

## 6. Scope

in:
- Refactor `Character` module to use DTOs and separate Domain/Entity models.
- Implement `StyleService` with Redis caching.
- Implement `DiceRollerClient` with Spring Cloud OpenFeign and Resilience4j.
- Create modular structure for `Auth` and `Admin`.

out:
- Full migration of all modules to Hexagonal Architecture (only `Character` as PoC).
- Deployment to Kubernetes (Docker Compose is sufficient).

## 7. Stakeholders & Roles

- AI Agent — Lead Developer — responsible for implementation — ai@rpgmanager.com
- User — Product Owner — responsible for requirements — user@rpgmanager.com

## 8. High-level timeline & milestones

1. M1 — Hexagonal PoC Complete — 2026-01-19 — AI Agent
2. M2 — Redis Caching Implemented — 2026-01-20 — AI Agent
3. M3 — External Integration (Spring Cloud) — 2026-01-21 — AI Agent
4. M4 — Domain Decomposition Prep — 2026-01-22 — AI Agent

## 9. Task list

- T-001 | Create `CharacterResponse` DTO and Mapper | AI Agent | complexity: S | deps: [] | done: true
- T-002 | Refactor `CharacterController` to return DTOs | AI Agent | complexity: S | deps: [T-001] | done: true
- T-003 | Configure Redis in `docker-compose.yml` | AI Agent | complexity: XS | deps: [] | done: true
- T-004 | Implement `StyleService` with `@Cacheable` | AI Agent | complexity: M | deps: [T-003] | done: true
- T-005 | Add Spring Cloud dependencies (Feign, Resilience4j) | AI Agent | complexity: S | deps: [] | done: false
- T-006 | Implement `DiceRollerClient` interface | AI Agent | complexity: M | deps: [T-005] | done: false
- T-007 | Create `RandomOrgAdapter` and Fallback | AI Agent | complexity: M | deps: [T-006] | done: false

## 10. Risks and mitigations

- R-001: `External API Rate Limits` | probability: high | impact: medium | mitigation: `Implement strict fallback to local random generator` | owner: `AI Agent`

## 11. Assumptions

- Docker is available on the host machine.
- Internet connection is available for Maven dependencies and Random.org API.

## 12. Implementation approach

We will use a "Strangler Fig" pattern approach where possible, introducing new architectural patterns side-by-side with existing ones.
For Hexagonal Architecture, we will introduce a `port.in` (Service/UseCase) and `port.out` (Repository) package structure within the `character` module.
Redis will be run as a container service `rpg-redis` on port 6379.

## 13. Testing & validation plan

- Unit tests for `StyleService` verifying cache interactions.
- Integration tests for `DiceRollerClient` using WireMock to simulate external API responses and failures.

## 14. Deployment plan & roll-back strategy

- Local deployment via `docker-compose up -d`.
- Rollback: `git revert` of specific commits if compilation fails.

## 15. Monitoring & observability

- Logs: Application logs checking for "Fallback triggered" messages.

## 16. Compliance, security & privacy considerations

- N/A

## 17. Communication plan

- N/A

## 18. Related documents & links

- N/A

## 19. Appendix

- N/A
