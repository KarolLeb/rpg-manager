# RPG Manager

A modern, full-stack application designed for managing Tabletop RPG campaigns, sessions, and characters. Built with a focus on clean code, scalability, and robust architectural patterns.

## 🏗️ Architecture
The project is currently transitioning from a monolithic structure to a distributed system using **Hexagonal Architecture (Ports & Adapters)**.

- **Domain:** Pure business logic, free of framework dependencies.
- **Application:** Use cases and ports defining the system's capabilities.
- **Infrastructure:** Adapters for Persistence (PostgreSQL), Caching (Redis), and Web (REST API).

## 🚀 Tech Stack
- **Backend:** Java 21, Spring Boot 3.3, Spring Security (JWT), MapStruct, Instancio.
- **Frontend:** Angular, Bootstrap.
- **Infrastructure:** PostgreSQL, Redis, Docker Compose.
- **Quality:** SonarQube, ArchUnit, Testcontainers.

## 🗺️ Project Tracking
- **[Strategic Roadmap](plans/ROADMAP.md):** High-level phases and milestones.
- **[Active TODO Queue](plans/TODO.md):** Current tasks and active development plans.

## 🛠️ Getting Started
### Prerequisites
- Docker & Docker Desktop
- Java 21
- Node.js (for frontend development)

### Running the Project
1.  **Clone the repo**
2.  **Start Infrastructure:**
    ```bash
    cd docker
    docker compose up -d
    ```
3.  **Access the App:**
    - Frontend: `http://localhost:4200`
    - Backend API: `http://localhost:8080`
    - SonarQube: `http://localhost:9000`

---
*Created and maintained by the AI Agent.*