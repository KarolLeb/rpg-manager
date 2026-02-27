# Project TODO Queue

Operational task list and active execution plans.

## 🚀 Active Plans
*(No active plans at the moment)*

## 📋 Backlog (Short Tasks)

## ✅ Recently Finished (Pending Archive)
- [x] **Property-Based Testing:** Introduced property-based testing using `jqwik` for `ActionPermissionService` (backend-core). Verified 6 resolution-chain invariants across random inputs and confirmed integration into the main CI/CD pipeline.
- [x] **Performance Budget:** Integrated custom Performance API checks into the Playwright E2E suite (TBT ≤ 200ms, FCP ≤ 1.2s).
- [x] **API Contract Coverage**: Implemented a lightweight TypeScript `OpenApiCoverageTracker` in Playwright that dynamically pulls OpenAPI specs exposed by `springdoc-openapi-starter-webmvc-api` on the backend and tracks intercepted endpoints to output coverage results locally.
- [x] **CRAP Analysis:** Built `crap-analyzer.js` script inside `.github/scripts` and integrated it with `backend/pom.xml` to parse Jacoco XML and strictly fail build for methods with CRAP > 30.
- [x] **E2E Quality Gate Enhancement:** Successfully achieved 50% line / 40% branch E2E coverage across all services. 
- [x] **Unit Test Quality Enforcement:** Reached ≥ 95% line and ≥ 90% branch coverage for both Backend (98.8% core) and Frontend (99.3%/97.3%). Enforced 20-point cognitive complexity limit and 75% (UI) / 90% (Core) mutation scores. Resolved all SonarQube Quality Gate violations.
- [x] **Toast Notification System:** Implemented signal-based `ToastService` and standalone `ToastComponent` for modern UX. Integrated with `AuthGuard` (RBAC), Login, Register, Campaigns, and Character Sheet. Updated CSP for Material Icons and updated unit/E2E tests (63/63 passing, including role-based access verification).
- [x] **Quality Gate:** Fixed SonarQube Quality Gate for Backend. Resolved coverage reporting for Auth/Admin modules and fixed new violations (Parameterized tests, lambda refactor).
- [x] **Microservices Extraction (Admin & Auth):** Extracted Auth and Admin services, decoupled Core service using Feign clients and JWT enrichment.
- [x] **E2E Integration Testing:** Replaced all API mocks in Playwright tests with real backend interactions. Fixed Docker multi-module build issues, improved test robustness (strict mode, serial execution), and enabled automated database schema resets (Flyway clean) for development. All 21 E2E tests passing.
- [x] **Frontend:** Visual indicators for cache hits (addressed via Grafana Redis Dashboard)


---
*For the long-term vision, see [ROADMAP.md](./ROADMAP.md).*