# RPG Manager Roadmap

Strategic milestones and high-level project evolution.

## Status Badges
- Planned: ![Planned](https://img.shields.io/badge/status-Planned-lightgrey)
- In Progress: ![In Progress](https://img.shields.io/badge/status-In%20Progress-blue)
- Done: ![Done](https://img.shields.io/badge/status-Done-brightgreen)

## 🗺️ Project Phases

### Phase 1: MVP Foundation ![Done](https://img.shields.io/badge/status-Done-brightgreen)
*   **Goal:** Establish core RPG management capabilities.
*   **Deliverables:**
    *   Monolithic Backend (Spring Boot).
    *   Angular Frontend with Dashboard.
    *   PostgreSQL Persistence.
    *   JWT Authentication.

### Phase 2: Architectural Hardening ![Done](https://img.shields.io/badge/status-Done-brightgreen)
*   **Goal:** Clean up technical debt and establish scalable patterns.
*   **Deliverables:**
    *   [x] Full Hexagonal Architecture (Ports & Adapters) rollout.
    *   [x] Redis Infrastructure & Caching integration.
    *   [x] MapStruct & Instancio tooling integration.
    *   [x] CI/CD optimization and "act" configuration.

### Phase 3: Distributed Scalability ![In Progress](https://img.shields.io/badge/status-In%20Progress-blue)
*   **Goal:** Decompose the monolith for independent scaling.
*   **Deliverables:**
    *   Extraction of `Auth` service.
    *   Extraction of `Admin` service.
    *   API Gateway implementation.

## 🏁 Recently Completed Milestones
- **Jan 2026:** Validated local CI/CD pipelines with `act`.
- **Jan 2026:** Achieved 100% Hexagonal Architecture compliance across all modules.
- **Jan 2026:** Integrated Redis for campaign list caching and verified eviction logic.
- **Jan 2026:** Established SonarQube quality gates for both Frontend and Backend.

---
*Last Updated: 2026-01-25*