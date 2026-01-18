# Plan: Backend Core Features (Campaigns & Sessions)

## 2. Short description

Implementation of core RPG Manager functionalities including Campaign management (CRUD) and Game Session scheduling/tracking.

## 3. Current status

```yaml
owner: AI Agent <ai@rpgmanager.com>
state: in-progress
last_updated: 2026-01-18
blockers: []
```

## 4. Objectives

1. Enable Game Masters to create and manage Campaigns.
2. Enable scheduling and status tracking of Game Sessions.
3. Link Characters to Campaigns.

## 5. Success criteria

- name: `Campaign Lifecycle`
  metric: `Functionality`
  target: `Create, Read, Update, Delete available via API`
  verification: `Playwright E2E tests`

## 6. Scope

in:
- Campaign Service & Controller
- Session Service & Controller
- Character-Campaign Association logic
out:
- Complex scheduling (calendar integration)

## 7. Stakeholders & Roles

- AI Agent — Lead Developer — responsible for implementation — ai@rpgmanager.com

## 8. High-level timeline & milestones

1. M1 — Campaign CRUD — 2026-01-25 — AI Agent
2. M2 — Session Logic — 2026-01-30 — AI Agent

## 9. Task list

- T-001 | Implement `CampaignService` (CRUD logic) | AI Agent | complexity: M | deps: [] | done: false
- T-002 | Implement `SessionService` (Schedule, Cancel, Complete) | AI Agent | complexity: M | deps: [] | done: false
- T-003 | Implement `CharacterService` (Link to Campaign) | AI Agent | complexity: S | deps: [T-001] | done: false

## 10. Risks and mitigations

- R-001: `Data Consistency` | probability: low | impact: high | mitigation: `Transactional annotations on service methods` | owner: `AI Agent`

## 11. Assumptions

- Basic entities (`Campaign`, `Session`) are already defined in the codebase.

## 12. Implementation approach

Standard Spring Boot Layered Architecture (Controller -> Service -> Repository).

## 13. Testing & validation plan

- Unit tests for Services.
- Integration tests for Controllers.

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
