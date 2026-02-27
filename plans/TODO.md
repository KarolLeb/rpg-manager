# Project TODO Queue

Operational task list and active execution plans.

## 🚀 Active Plans
*(No active plans at the moment)*

## 📋 Backlog (Short Tasks)
- [ ] **API Contract Coverage:** Implement Spring Cloud Contract or Swagger-coverage to track E2E/Contract coverage of OpenAPI spec.
- [ ] **Performance Budget:** Integrate Playwright's Lighthouse or custom Performance API checks into the E2E suite (TBT ≤ 200ms, FCP ≤ 1.2s).
- [ ] **CRAP Analysis:** Setup and configure a CRAP (Change Risk Anti-Patterns) metric analysis tool for the codebase to identify overly complex and under-tested code methods.
- [ ] **Property-Based Testing:** Introduce property-based testing (e.g., using jqwik for Java) to complement existing example-based unit tests for core domain logic.

## ✅ Recently Finished (Pending Archive)
- [x] **E2E Quality Gate Enhancement:** Successfully achieved 50% line / 40% branch E2E coverage across all services. 
- [x] **Unit Test Quality Enforcement:** Reached ≥ 95% line and ≥ 90% branch coverage for both Backend (98.8% core) and Frontend (99.3%/97.3%). Enforced 20-point cognitive complexity limit and 75% (UI) / 90% (Core) mutation scores. Resolved all SonarQube Quality Gate violations.
- [x] **Toast Notification System:** Implemented signal-based `ToastService` and standalone `ToastComponent` for modern UX. Integrated with `AuthGuard` (RBAC), Login, Register, Campaigns, and Character Sheet. Updated CSP for Material Icons and updated unit/E2E tests (63/63 passing, including role-based access verification).
- [x] **Quality Gate:** Fixed SonarQube Quality Gate for Backend. Resolved coverage reporting for Auth/Admin modules and fixed new violations (Parameterized tests, lambda refactor).
- [x] **Microservices Extraction (Admin & Auth):** Extracted Auth and Admin services, decoupled Core service using Feign clients and JWT enrichment.
- [x] **E2E Integration Testing:** Replaced all API mocks in Playwright tests with real backend interactions. Fixed Docker multi-module build issues, improved test robustness (strict mode, serial execution), and enabled automated database schema resets (Flyway clean) for development. All 21 E2E tests passing.
- [x] **Frontend:** Visual indicators for cache hits (addressed via Grafana Redis Dashboard)


---
*For the long-term vision, see [ROADMAP.md](./ROADMAP.md).*