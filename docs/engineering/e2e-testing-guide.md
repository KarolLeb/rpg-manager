# E2E Integration Testing Guide

This document describes the End-to-End (E2E) testing strategy for the RPG Manager project. Our E2E tests are "integrated," meaning they run against the real backend services and database instead of using API mocks.

## 🚀 Key Principles

1.  **No Mocks**: Tests should interact with the real system as much as possible. This catches integration issues between the Frontend and Backend services.
2.  **Seeded Data**: Tests rely on a known initial state provided by database migrations (`V2__insert_test_data.sql`).
3.  **Isolation**: While we use a real database, tests should attempt to be self-sufficient (e.g., creating unique usernames using timestamps) or rely on stable seeded records.
4.  **Serial Execution**: Tests that modify shared records (e.g., characters) are configured to run in `serial` mode to prevent race conditions.

## 🛠️ Environment Setup

The E2E tests require the full Docker stack to be active.

### Service Mapping
Requests from the frontend are routed through the Angular proxy (`proxy.conf.json`) to the following internal Docker services:
- `/api/auth` -> `http://backend-auth:8081`
- `/api/admin` -> `http://backend-admin:8082`
- `/api` -> `http://backend-core:8080`

### Flyway & Database
In development, we use `SPRING_FLYWAY_CLEAN_ON_VALIDATION_ERROR=true` to automatically reset the database schema if migration checksums mismatch during iterative development.

## 📋 Running Tests

### All Tests
```powershell
cd frontend
npx playwright test
```

### Specific Test File
```powershell
npx playwright test e2e/auth.spec.ts
```

### UI Mode (Interactive Debugging)
```powershell
npx playwright test --ui
```

## 🔍 Debugging & Troubleshooting

### Backend Logs
If a test fails with a 401 or 500 error, check the backend logs in real-time:
```powershell
cd infra/app
docker compose logs -f backend-auth
# or
docker compose logs -f backend-core
```

### Common Failures

| Issue | Potential Cause | Fix |
| :--- | :--- | :--- |
| `strict mode violation` | Multiple elements match a selector (e.g., several headings with same text). | Use more specific locators like `page.locator('.character-card', { hasText: 'Geralt' })`. |
| `Unauthorized (401)` | Token expired or incorrect credentials in test. | Verify credentials match those in `V2__insert_test_users.sql` or the registration flow. |
| `Flyway Validate failed` | Database schema out of sync. | Run `docker compose down -v` to clear volumes, then `docker compose up -d`. |
| Test "flakiness" | Network latency or race conditions in DB. | Use `test.describe.configure({ mode: 'serial' })` for sensitive flows and increase `timeout`. |

## 🏗️ Adding New Tests

When adding a new E2E test:
1.  **Use specific IDs**: Ensure interactive elements have unique IDs.
2.  **Avoid hardcoding IDs**: Prefer text-based locators or ARIA roles for better resilience.
3.  **Cleanup**: If your test creates data, attempt to clean it up or ensure it doesn't collide with subsequent runs by using unique prefixes (e.g., `Date.now()`).
