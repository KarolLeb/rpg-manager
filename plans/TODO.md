# Project TODO Queue

Operational task list and active execution plans.

## 🚀 Active Plans
*(No active plans at the moment)*

## 📋 Backlog (Short Tasks)

## ✅ Recently Finished (Pending Archive)
- [x] **Toast Notification System:** Implemented signal-based `ToastService` and standalone `ToastComponent` for modern UX. Integrated with `AuthGuard` (RBAC), Login, Register, Campaigns, and Character Sheet. Updated CSP for Material Icons and updated unit/E2E tests (63/63 passing, including role-based access verification).
- [x] **Quality Gate:** Fixed SonarQube Quality Gate for Backend. Resolved coverage reporting for Auth/Admin modules and fixed new violations (Parameterized tests, lambda refactor).
- [x] **Microservices Extraction (Admin & Auth):** Extracted Auth and Admin services, decoupled Core service using Feign clients and JWT enrichment.
- [x] **E2E Integration Testing:** Replaced all API mocks in Playwright tests with real backend interactions. Fixed Docker multi-module build issues, improved test robustness (strict mode, serial execution), and enabled automated database schema resets (Flyway clean) for development. All 21 E2E tests passing.
- [x] **Frontend:** Visual indicators for cache hits (addressed via Grafana Redis Dashboard)


---
*For the long-term vision, see [ROADMAP.md](./ROADMAP.md).*