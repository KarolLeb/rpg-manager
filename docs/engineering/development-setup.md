# Development Setup

This document describes how to set up the development environment using Docker.

## 🛠️ Docker Environments

The project uses two separate Docker Compose setups to balance resource usage and convenience.

### 1. Project Infrastructure (`/infra/app`)
Core dependencies required to run the application components.
- **Path:** `C:\rpg-manager\infra\app`
- **Services:** 
  - `postgres`: Main database
  - `redis`: Caching and messaging
  - `pgadmin`: Database management UI
  - `backend`: Spring Boot application
  - `frontend`: Angular application
- **Usage:**
  ```powershell
  cd infra/app
  docker-compose up -d
  ```

### 2. Quality Analysis Tools (`/infra/dev`)
Tools for static code analysis.
- **Path:** `C:\rpg-manager\infra\dev`
- **Services:**
  - `sonarqube`: Static code analysis
- **Usage:**
  ```powershell
  cd infra/dev
  docker-compose up -d
  ```

### 3. Monitoring & Observability (`/infra/monitoring`)
Production-like monitoring stack.
- **Path:** `C:\rpg-manager\infra\monitoring`
- **Services:**
  - `prometheus`: Metrics collection
  - `grafana`: Metrics visualization
- **Usage:**
  ```powershell
  cd infra/monitoring
  docker-compose up -d
  ```

## 🧪 Local CI (act)

You can run GitHub Actions workflows locally using [act](https://github.com/nektos/act). This project includes a `.actrc` configuration file to ensure compatibility.

### Prerequisites
- [Docker](https://www.docker.com/)
- [act](https://github.com/nektos/act)

### Usage
Run all workflows triggered by a push:
```powershell
act
```

Run a specific job (e.g., backend):
```powershell
act -j backend
```

Run with a clean start (if you encounter issues with reused containers):
```powershell
act --rm
```

## 🔗 Access Points

| Service | URL | Note | 
| :--- | :--- | :--- | 
| **Frontend** | [http://localhost:4200](http://localhost:4200) | | 
| **Backend API** | [http://localhost:8080](http://localhost:8080) | | 
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | | 
| **pgAdmin** | [http://localhost:5050](http://localhost:5050) | `admin@admin.com` / `admin` | 
| **SonarQube** | [http://localhost:9000](http://localhost:9000) | | 
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin` / `admin` | 
| Prometheus | [http://localhost:9090](http://localhost:9090) | | 

## 🛡️ SonarQube Workflows

The project provides standardized scripts in the root `package.json` to manage code quality analysis. These commands ensure that analysis data (coverage, linting, mutation) is consistent and up-to-date.

### Command Profiles

| Profile | Action | Use Case |
| :--- | :--- | :--- |
| **`scan`** | Uploads existing reports to SonarQube without running tests. | Re-uploading after manual test runs. |
| **`quick`** | Runs Linting/Spotless + Unit Tests + Coverage + Scan. | Standard pre-push or CI check. |
| **`full`** | Runs **Quick** + Mutation Testing (PIT/Stryker) + Scan. | Deep quality verification (Nightly/PR). |

### Usage

Run analysis for both stacks:
```powershell
npm run sonar:quick
# or for a deep check:
npm run sonar:full
```

Target a specific stack:
```powershell
npm run sonar:quick:backend
npm run sonar:quick:frontend
```

Just upload existing data:
```powershell
npm run sonar:scan
```

> [!NOTE]
> **Performance:** `sonar:full` (Mutation Testing) is computationally intensive and can take significant time. It is recommended for CI/CD or occasional local deep-dives.

## 🧪 Testing

### Backend
Run unit and integration tests using Maven:
```powershell
cd backend
./mvnw test
```

Run Mutation Testing (PITest) to verify assertion quality:
```powershell
cd backend
./mvnw pitest:mutationCoverage
```
The report will be generated in `backend/target/pit-reports/index.html`.

### Frontend
Run unit tests with Karma:
```powershell
cd frontend
npm test
```

### End-to-End (E2E)
Run E2E tests using Playwright. 

> [!TIP]
> **Port 4200:** Playwright is configured with `reuseExistingServer: true`. If the application is already running (e.g., via Docker or `ng serve`), Playwright will use that instance. If not, it will attempt to start a new one automatically.
```powershell
# 1. Stop main app
cd infra/app
docker compose down

# 2. Run E2E tests
cd ../frontend
npm run e2e:cli
```
