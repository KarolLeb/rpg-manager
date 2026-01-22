# ⚔️ RPG Manager

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-19-red.svg)](https://angular.io/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](#-architecture)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, full-stack application designed for Tabletop RPG (TTRPG) enthusiasts. **RPG Manager** helps Game Masters and players organize campaigns, track sessions, and maintain detailed character sheets with ease.

## ✨ Key Features

- **🏰 Campaign Orchestration:** Create and manage complex TTRPG campaigns.
- **📅 Session Tracking:** Log history, outcomes, and progress of every gaming session.
- **📜 Dynamic Character Sheets:** Detailed stat tracking with support for different character types (Permanent/Temporary).
- **🎭 Role-Based Access:** Distinct workflows for Game Masters (GMs) and Players.
- **⚡ High Performance:** Integrated **Redis** caching for lightning-fast retrieval of frequent data.
- **🛡️ Secure by Design:** JWT-based authentication and role-enforced API endpoints.

## 🏗️ Architecture

The project is built using **Hexagonal Architecture (Ports & Adapters)**, ensuring that core business logic remains independent of frameworks, databases, and external UI.

```mermaid
graph TD
    subgraph "Infrastructure Layer (Adapters)"
        Web[REST Controllers]
        DB[(PostgreSQL)]
        Cache[(Redis)]
    end

    subgraph "Application Layer"
        UC[Use Cases / Services]
        Ports[Ports / Interfaces]
    end

    subgraph "Domain Layer"
        Model[Domain Models / POJOs]
    end

    Web --> Ports
    UC --> Ports
    Ports --> Model
    DB -.-> Ports
    Cache -.-> Ports
```

- **[Deep Dive into Architecture](docs/engineering/hexagonal-architecture.md)**

## 🚀 Tech Stack

### Backend
- **Core:** Java 21, Spring Boot 3.3
- **Security:** Spring Security, JWT
- **Tooling:** MapStruct (Mapping), Instancio (Test Data)
- **Caching:** Redis

### Frontend
- **Core:** Angular 19, TypeScript
- **Styling:** Bootstrap, SCSS

### Quality & DevOps
- **Analysis:** SonarQube
- **Persistence:** PostgreSQL
- **Environment:** Docker Compose
- **Testing:** JUnit 5, Mockito, Testcontainers, Playwright (E2E)

## 🗺️ Project Tracking

- **[Strategic Roadmap](plans/ROADMAP.md):** High-level phases and long-term milestones.
- **[Active TODO Queue](plans/TODO.md):** Current tasks and active development plans.

## 🛠️ Getting Started

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Java 21](https://adoptium.net/temurin/releases/?version=21)
- [Node.js](https://nodejs.org/) (v20+)

### Quick Start
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/rpg-manager.git
    cd rpg-manager
    ```
2.  **Launch the infrastructure:**
    ```bash
    cd docker
    docker compose up -d
    ```
3.  **Access the Application:**
    - **Frontend:** `http://localhost:4200`
    - **API Docs:** `http://localhost:8080/actuator`
    - **SonarQube Dashboard:** `http://localhost:9000`

---
*Developed and maintained with precision by the AI Agent.*
