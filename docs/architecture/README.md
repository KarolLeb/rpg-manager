# Architecture Directory

This directory describes the system architecture, focusing on the transition to a decoupled, testable system.

## 🏗️ Core Architecture

The system is built using **Hexagonal Architecture (Ports & Adapters)**. This pattern ensures that business logic is isolated from infrastructure concerns (DB, Web, Caching).

### Interaction Flow

```mermaid
sequenceDiagram
    participant UI as Angular Frontend
    participant AdapterIn as Web Adapter (REST)
    participant PortIn as Input Port (UseCase Interface)
    participant App as Application Service
    participant Domain as Domain Model
    participant PortOut as Output Port (Repository Interface)
    participant AdapterOut as Persistence Adapter (JPA)
    participant DB as PostgreSQL

    UI->>AdapterIn: GET /api/campaigns
    AdapterIn->>PortIn: getAllCampaigns()
    PortIn->>App: getAllCampaigns()
    App->>PortOut: findAll()
    PortOut->>AdapterOut: findAll()
    AdapterOut->>DB: SQL SELECT
    DB-->>AdapterOut: Result Set
    AdapterOut-->>PortOut: CampaignEntities
    PortOut-->>App: CampaignDomain Objects
    App-->>PortIn: List<CampaignDTO>
    PortIn-->>AdapterIn: List<CampaignDTO>
    AdapterIn-->>UI: 200 OK (JSON)
```

- **[Hexagonal Architecture Details](../engineering/hexagonal-architecture.md):** Deep dive into the pattern, package structure, and rules enforced in this project.

## 📝 Architecture Decision Records

- [Architecture Decision Records (ADRs)](../ADRs/README.md)

<!-- © Capgemini 2025 -->
