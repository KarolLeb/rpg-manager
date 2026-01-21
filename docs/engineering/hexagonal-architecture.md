# Hexagonal Architecture Standard

## Overview
This document defines the standard for Hexagonal Architecture (Ports and Adapters) in the RPG Manager project. The goal is to decouple business logic from external frameworks, databases, and UI.

## Package Structure
Each module (e.g., `campaign`, `character`) should follow this structure:

`com.rpgmanager.backend.<module>`
├── `domain`
│   ├── `model`         # Pure business objects (No JPA annotations)
│   ├── `repository`    # Interface ports for persistence (output ports)
│   └── `exception`     # Domain-specific exceptions
├── `application`
│   ├── `port`          # Input ports (interfaces for use cases)
│   │   ├── `in`        # Use case interfaces
│   │   └── `out`       # (Optional) if more specific ports are needed
│   ├── `service`       # Implementation of input ports (orchestrates domain objects)
│   └── `dto`           # Data Transfer Objects for application layer
└── `infrastructure`
    ├── `adapter`
    │   ├── `in`        # Input adapters (e.g., Controllers, REST API)
    │   └── `out`       # Output adapters (e.g., JPA Repositories, Entities)
    ├── `mapper`        # Mappers between domain models, DTOs, and entities
    └── `config`        # Spring-specific configuration for the module

## Dependency Rules
1. **Domain** layer must not depend on any other layer. It must be free of Spring and JPA annotations.
2. **Application** layer can depend on the **Domain** layer.
3. **Infrastructure** layer can depend on both **Application** and **Domain** layers.
4. Dependency injection should be used to provide implementations of ports to the application layer.

## Naming Conventions
- **Input Ports (Use Cases)**: `<Action><Module>UseCase` (e.g., `CreateCampaignUseCase`)
- **Output Ports (Repositories)**: `<Module>Repository` (e.g., `CampaignRepository`)
- **Entities**: `<Module>Entity` (to distinguish from domain models if JPA is used)
- **Adapters**: `<Tech><Module>Adapter` or specific naming like `<Module>Controller`

## Implementation Steps
1. Define the **Domain Model** (Pure POJO).
2. Define the **Output Port** (Repository Interface) in the domain/repository package.
3. Define the **Input Port** (UseCase Interface) in the application/port package.
4. Implement the **UseCase** in the application/service package.
5. Implement the **Output Adapter** (JPA Entity + Repository Implementation) in the infrastructure/adapter/out package.
6. Implement the **Input Adapter** (REST Controller) in the infrastructure/adapter/in package.
7. Configure Spring (using `@Service`, `@Repository` or a Java config class) to wire them together.
