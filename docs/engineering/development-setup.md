# Development Setup

This document describes how to set up the development environment using Docker.

## 🛠️ Docker Environments

The project uses two separate Docker Compose setups to balance resource usage and convenience.

### 1. Project Infrastructure (`/docker`)
Core dependencies required to run the application components.
- **Path:** `E:\rpg-manager\docker`
- **Services:** 
  - `postgres`: Main database
  - `redis`: Caching and messaging
  - `pgadmin`: Database management UI
  - `backend`: Spring Boot application
  - `frontend`: Angular application
- **Usage:**
  ```powershell
  cd docker
  docker-compose up -d
  ```

### 2. Persistent Dev Tools (`/dev-tools`)
Tools that are typically started once and kept running across multiple development sessions.
- **Path:** `E:\rpg-manager\dev-tools`
- **Services:**
  - `sonarqube`: Static code analysis
  - `prometheus`: Metrics collection
  - `grafana`: Metrics visualization
- **Usage:**
  ```powershell
  cd dev-tools
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
| **Prometheus** | [http://localhost:9090](http://localhost:9090) | | 
