# Testcontainers & Docker Desktop Compatibility Report

## Issue Summary
Users may encounter failures when running Java Testcontainers with recent versions of Docker Desktop (e.g., 4.56.0 / Engine v29+). The error manifests as:

> `java.lang.IllegalStateException: Could not find a valid Docker environment`
> `BadRequestException (Status 400: {"message":"client version 1.32 is too old. Minimum supported API version is 1.44"})`

## Root Cause
1.  **Docker Engine Update:** Recent Docker Engine releases (v26+) have aggressively deprecated and removed support for older API versions (< 1.24, and eventually < 1.44 for certain endpoints).
2.  **Testcontainers/Docker-Java Default:** The `docker-java` library used by Testcontainers 1.x (up to 1.20.4) defaults to API version `1.32` during its auto-discovery process on Windows named pipes (`npipe://`).
3.  **The Conflict:** The Java client attempts to connect using API v1.32, but the new Docker Engine rejects this as "too old," leading to a 400 Bad Request error and a failure to initialize the Docker environment.

## Verified Solution: Downgrade Docker Desktop
The most reliable immediate fix is to downgrade Docker Desktop to a version that still supports the older API version used by the Java client.

*   **Recommended Version:** Docker Desktop 4.30.0
*   **Engine Version:** 26.1.1 (Supports API v1.24+)
*   **Download Link:** [Docker Desktop 4.30.0 for Windows](https://desktop.docker.com/win/main/amd64/149282/Docker%20Desktop%20Installer.exe)

**Steps:**
1.  Uninstall the current Docker Desktop version.
2.  Install Docker Desktop 4.30.0.
3.  **Crucial:** Disable "Automatically check for updates" in Docker Desktop Settings to prevent it from auto-updating back to an incompatible version.

## Alternative Solution: Upgrade Testcontainers (Long Term)
*   **Upgrade to Testcontainers 2.0+:** Future major versions of Testcontainers and `docker-java` will officially support the newer Docker Engine APIs.
*   **Manual Configuration:** For older Testcontainers versions, creating a `$HOME/.docker-java.properties` file with `api.version=1.44` can sometimes force the client to use a compatible version, though this has mixed results depending on the specific transport (npipe vs. tcp).

## Verification
A working setup can be verified using a simple JUnit 5 test with `org.testcontainers:postgresql`. If the test passes without the 400 error, the compatibility issue is resolved.
