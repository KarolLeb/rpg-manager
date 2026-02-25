# Troubleshooting & Engineering Notes

This document records technical issues encountered during development and their solutions to prevent recurrence.

## Backend

### 1. Postgres JSONB Mapping
**Issue:** `ERROR: column "stats" is of type jsonb but expression is of type character varying`
**Context:** When saving a `String` field to a `jsonb` column in Postgres using Hibernate 6.
**Solution:** You must explicitly annotate the field with `@JdbcTypeCode(SqlTypes.JSON)`.
```java
@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb")
private String stats;
```
**Lesson:** Hibernate does not automatically cast String to JSONB even if `columnDefinition` is set.

### 2. Spring Security & Hidden Errors
**Issue:** Public endpoints return `401 Unauthorized` instead of `500` or `400` when an exception occurs.
**Cause:** When a Controller throws an exception, Spring forwards the request to `/error`. If `/error` is not explicitly permitted in `SecurityConfig`, the `AuthenticationEntryPoint` blocks it.
**Solution:** Allow anonymous access to the error endpoint.
```java
.requestMatchers("/error").permitAll()
```

### 3. JwtFilter & Public Endpoints
**Issue:** Invalid tokens (expired/malformed) cause `401` even on `permitAll` endpoints.
**Cause:** Exceptions thrown inside a Filter (e.g., `io.jsonwebtoken.ExpiredJwtException`) bubble up to the `AuthenticationEntryPoint` before the authorization decision is made.
**Solution:** Wrap token parsing in a `try-catch` block inside `JwtFilter`. If the token is invalid, log/ignore it and continue the chain anonymously. Let the downstream Authorization filter decide if the request should be allowed.

### 4. Docker Volumes on Windows
**Issue:** `Flyway validation failed: Checksum mismatch` persists even after running `docker-compose down -v`.
**Cause:** File locking on Windows can prevent Docker from actually deleting volume data.
**Solution:** 
1.  Enable `SPRING_FLYWAY_CLEAN_ON_VALIDATION_ERROR=true` as an environment variable in `docker-compose.yml`.
2.  Set `SPRING_FLYWAY_CLEAN_DISABLED=false`.
This forces Flyway to wipe the schema when it detects a mismatch (e.g., when checksums change during development), effectively resetting the DB state automatically.

### 5. Backend Multi-module Docker Builds
**Issue:** Maven build failing inside Docker with `Could not find artifact com.rpgmanager:backend-common`.
**Cause:** Running `mvn spring-boot:run` from a sub-directory prevents Maven from resolving local sibling modules that haven't been installed to a repository.
**Solution:** 
1. Set `WORKDIR` to the project root (`/app`).
2. Run with project list and also-make flags: `mvn spring-boot:run -pl backend-auth -am`.
3. Ensure sibling modules (like `backend-common`) are installed in the local Maven cache during the build stage using `mvn install -N -DskipTests` (for parent) and `mvn install -pl backend-common -DskipTests`.


## Frontend

### 1. UUID vs ID
**Issue:** Character sheets failing to load (`/character/undefined`).
**Cause:** Frontend model expected numeric `id`, but backend DTO returned UUIDs (and missed the numeric ID).
**Decision:** Switched to using **UUIDs** for all public-facing routing and API calls (`/api/characters/{uuid}`). Updated Frontend models and services to use `string` (UUID) instead of `number`.

### 2. JSON Parsing Safety
**Issue:** White screen on startup.
**Cause:** `JSON.parse` in `AuthService` threw an error on corrupted localStorage data.
**Solution:** Always wrap `JSON.parse(localStorage.getItem(...))` in a `try-catch` block and clear the storage on error.

### 3. E2E Tests: Port 4200 Conflict
**Issue:** `Error: http://localhost:4200 is already used` when running Playwright tests.
**Cause:** Playwright is configured to start its own Angular dev server. If the frontend is already running (e.g., via Docker or `ng serve`), the port is occupied.
**Solution:** 
The project is now configured with `reuseExistingServer: true` in `playwright.config.ts`. This allows Playwright to reuse an existing server instance. 

If you still encounter issues or want to ensure a completely clean test run against the latest code, stop the main application first:
```bash
cd docker
docker compose down
```