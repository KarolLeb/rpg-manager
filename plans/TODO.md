# RPG Manager - Master Roadmap

This document serves as an index to the active detailed plans. All core project objectives have been met.

## 🚀 Active Plans

### 1. Redis Infrastructure Verification
**File:** [`plans/redis-verification.md`](./redis-verification.md)
*Focus: Confirming Redis connectivity and cache functionality.*

### 2. Hexagonal Architecture Rollout
**File:** [`plans/hexagonal-architecture-rollout.md`](./hexagonal-architecture-rollout.md)
*Focus: Researching and implementing Ports & Adapters pattern.*

### 3. Microservices Extraction (Admin & Auth)
**File:** [`plans/microservices-extraction.md`](./microservices-extraction.md)
*Focus: Decomposing the monolith into Auth and Admin services.*

### 4. Documentation & Presentation
*   **Task:** Refine Project README for GitHub profile readiness.
*   **Focus:** Adding architecture diagrams, visual screenshots (placeholders), and a polished "Getting Started" guide.

### 5. Developer Experience (DX) Improvements (Backlog)
*   **Task:** Integrate SpringDoc OpenAPI for automated Swagger UI documentation.
*   **Task:** Integrate Spotless for automated code formatting and style enforcement.
*   [x] **Task:** Configure SonarQube Infrastructure - Scans successful for both Backend & Frontend.
*   **Task:** Setup Recommended MCPs (Everything MCP, Docker) - See [`docs/engineering/mcp-setup-guide.md`](../docs/engineering/mcp-setup-guide.md).
*   **Task:** 'act' further config - Local GitHub Actions execution and optimization.

---

## 🏛️ Archive (Completed)

- [`plans/archive/2026-01-initial-setup.md`](./archive/2026-01-initial-setup.md) - Initial Setup, Auth Logic, Database Schema.
- [`plans/archive/backend-core.md`](./archive/backend-core.md) - Campaign & Session CRUD Logic.
- [`plans/archive/frontend-implementation.md`](./archive/frontend-implementation.md) - Angular UI, Auth Views, Dashboards.
- [`plans/archive/architecture-refactor.md`](./archive/architecture-refactor.md) - Hexagonal Arch, Redis, Spring Cloud Integration.
- [`plans/archive/quality-gate.md`](./archive/quality-gate.md) - Code Coverage, CI Integration.
- [`plans/archive/campaign-crud-implementation.md`](./archive/campaign-crud-implementation.md) - (Redundant) Campaign CRUD.

---
*Last Updated: 2026-01-22*
