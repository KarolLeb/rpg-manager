# Plan: Microservices Extraction (Admin & Auth)

## 2. Short description
Strategically decompose the current monolithic backend by extracting the `Admin` and `Auth` (Login) domains into independent, deployable microservices. This increases scalability, isolation, and allows for independent development cycles.

## 3. Current status
```yaml
owner: AI Agent
state: in progress
last_updated: 2026-01-25
blockers: []
```

## 4. Objectives
1.  **Decompose:** Isolate the `Auth` logic (authentication/authorization) into a dedicated service.
2.  **Isolate:** Move `Admin` functionalities to a separate service to reduce the footprint of the core RPG logic.
3.  **Integrate:** Establish communication patterns (REST/Feign) between the new services and the Core RPG service.

## 5. Success criteria
- name: `Service Independence`
  metric: `Deployment`
  target: `Auth and Admin services can be started/stopped independently of Core`
  verification: `Docker Compose scaling and logs`

- name: `Data Isolation`
  metric: `Schema Separation`
  target: `Each service owns its own database schema (or separate DB)`
  verification: `Database inspection`

## 6. Scope
**In Scope:**
- Creating new Spring Boot projects/modules for `rpg-auth` and `rpg-admin`.
- Refactoring `SecurityConfig` in Core to trust the Auth service (or shared JWT).
- Updating `docker-compose.yml` to include new services.
- Implementing an API Gateway (optional, or using Nginx/Traefik/Spring Cloud Gateway) to route traffic.

**Out of Scope:**
- Full Event-Driven Architecture (EDA) implementation (Kafka/RabbitMQ) - stick to HTTP initially.
- Kubernetes deployment.

## 9. Task list
- T-001 | Analyze dependencies and boundaries for Auth and Admin modules | AI Agent | complexity: S | done: true
- T-001.5 | Refactor Core Entities (`Campaign`, `Character`) to store `userId` instead of `UserEntity` relation | AI Agent | complexity: M | deps: [T-001] | done: true
- T-002 | Scaffold new Spring Boot project for `rpg-auth-service` | AI Agent | complexity: M | deps: [T-001] | done: false
- T-003 | Migrate User/Auth logic and DB schema to `rpg-auth-service` | AI Agent | complexity: L | deps: [T-002] | done: false
- T-004 | Scaffold new Spring Boot project for `rpg-admin-service` | AI Agent | complexity: M | deps: [T-001] | done: false
- T-005 | Update Core Service to delegate Auth to `rpg-auth-service` | AI Agent | complexity: L | deps: [T-003] | done: false
- T-006 | Update `docker-compose.yml` to orchestrate the microservices ecosystem | AI Agent | complexity: S | deps: [T-004, T-005] | done: false

## 12. Implementation approach
We will transition to a **Multi-Module Maven Project** structure (parent `rpg-manager` with modules `backend-core`, `backend-auth`, `backend-admin`) or separate repos. Given the current structure, a multi-module reactor is likely best.
We will effectively split the `backend` folder into multiple services.
