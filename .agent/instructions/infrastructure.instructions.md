# Infrastructure & Deployment Instructions

## Docker Environment
The project is fully containerized using Docker Compose.

### Services
*   `postgres`: Database (port 5432)
*   `pgadmin`: DB GUI (port 5050)
*   `backend`: Spring Boot App (port 8080) - dev watch mode enabled.
*   `frontend`: Angular App (port 4200) - dev watch mode enabled.
*   `prometheus`: Metrics collection (port 9090)
*   `grafana`: Metrics visualization (port 3000)

## Workflow

### Startup (Recommended)
Run inside the `docker/` directory:
```bash
docker-compose up -d
```
This starts the entire environment. Backend and Frontend have "watch" mode enabled, so code changes are synchronized automatically.

### Troubleshooting
*   **Logs:** If a service fails, check logs: `docker-compose logs backend` (or `frontend`, `postgres`, etc.).
*   **Restart:** Use `docker-compose down` followed by `docker-compose up -d` to reset the environment.
*   **Data:** Database data is persisted in `docker/pg-data`. Remove this folder to reset the DB (only when containers are down).
