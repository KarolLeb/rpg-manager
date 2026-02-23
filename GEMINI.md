# RPG Manager - Project Entry Point

This file provides a high-level map of the project structure and resources for the AI Agent.

## 🤖 AI Agent Resources
- **[Instructions](.github/instructions.md)**: Central entry point for all coding standards, tech stack details, and sub-instructions (Backend, Frontend, Docs).
- **[Prompts](.github/prompts/)**: Reusable prompt templates for common tasks.

## 🛠️ Environment & Troubleshooting
- **Docker & Testcontainers**: If Testcontainers fail to start, ensure Docker is running.
- **Compatibility**: See **[Testcontainers Compatibility](docs/TESTCONTAINERS_COMPATIBILITY.md)** for known issues with Docker Desktop versions.
- **Action**: If Docker is not running, ask the user to start it before proceeding with tests.

## 🌿 Branching Strategy
- **New Features**: Starting work on a new feature must ALWAYS be done on a new, dedicated branch.
- **Bug Fixes & Separate Tasks**: When starting work on something else (such as simple bug fixes or unrelated tasks), you MUST create and use separate branches. Do not mix unrelated changes in a single branch.

## 🧪 Quality Assurance Workflows
Use these standardized npm scripts (defined in root `package.json`) to verify code quality. They cover both Backend and Frontend.

- **`npm run sonar:quick`**: **Standard Check**. Runs Linting, Unit Tests, and Coverage before scanning. Use this for pre-push verification.
- **`npm run sonar:full`**: **Deep Check**. Runs everything in Quick + **Mutation Testing** (PIT/Stryker). Use this for critical logic verification.
- **`npm run sonar:scan`**: **Upload Only**. Uploads existing reports without re-running tests.

### 🎭 End-to-End (E2E) Testing
Run E2E tests using Playwright (requires the frontend to be buildable):
- **`cd frontend; npm run e2e`**: Runs all E2E tests in headless mode.
- **`cd frontend; npm run e2e:cli`**: Runs E2E tests with list reporter (best for terminal output).
- **`cd frontend; npm run e2e:ui`**: Opens Playwright UI for interactive debugging.

## 📅 Project Management
- **[Plans & TODO](plans/TODO.md)**: Current development roadmap and task tracking.

## 📖 Documentation
- **[Architecture & Requirements](docs/)**: Contains ADRs, PRDs, and technical design documents.
- **[Contributing](CONTRIBUTING.md)**: Project setup and workflow guidelines.

---
*Follow the guidelines defined in `.github/instructions.md` for all development tasks.*

---

# Detailed Repository Context

## 1. Custom Agents

The repository includes specialized agents located in `.github/agents/`:

- **Developer**: Focuses on test-driven development with quality gates and design-first methodology
- **Code Reviewer**: Implements systematic code review with best practices enforcement
- **Tester**: Emphasizes BDD-focused testing approach with comprehensive test coverage

## 2. Reusable Prompt Templates

Located in `.github/prompts/`, these templates provide standardized approaches for:

- **`write-adr.prompt.md`**: Creates Architectural Decision Records with structured templates
- **`write-prd.prompt.md`**: Generates Product Requirements Documents with measurable objectives
- **`write-docs.prompt.md`**: Produces consistent documentation following repository standards
- **`write-ears-spec.prompt.md`**: Creates requirements using Easy Approach to Requirements Syntax
- **`copilot-setup-check.prompt.md`**: Evaluates and optimizes Copilot configuration

## 3. Repository Structure & Templates

The `docs/` directory provides organized templates for:

- **ADRs** (`docs/ADRs/`): Architecture decision documentation with context and consequences
- **PRDs** (`docs/PRDs/`): Product requirements with priorities and acceptance criteria
- **Design Documents** (`docs/design/`): Technical design specifications
- **Engineering Guidelines** (`docs/engineering/`): Development process documentation

## 4. Project Planning Framework

The `plans/` directory includes:

- **Plan Templates**: Structured project planning with stakeholders and timelines
- **Roadmap Management**: Strategic planning and milestone tracking
- **TODO Management**: Task tracking and completion monitoring

## 5. Document Reference Hierarchy

This diagram shows how documents reference each other across the repository, mapping the interconnections between configuration files, instructions, prompts, documentation, and plans.

```mermaid
flowchart LR
    %% Core Configuration Hub (Left)
    subgraph CONFIG ["🏠 Core Configuration"]
        CI["copilot-instructions.md"]
        README["README.md"]
    end

    %% GitHub Configuration (Top Center)
    subgraph GITHUB [".github/ Configuration"]
        direction TB

        subgraph AGENTS ["🤖 Agents"]
            DEV["Developer"]
            CR["CodeReview"]
            TEST["Testing"]
        end

        subgraph INSTRUCTIONS ["📋 Instructions"]
            DOCINST["docs.instructions"]
            BACKEND["backend.instructions"]
            FRONTEND["frontend.instructions"]
            BDD["bdd-tests.instructions"]
        end

        subgraph PROMPTS ["🎯 Prompts"]
            DOC_PROMPT["write-docs"]
            ADR_PROMPT["write-adr"]
            PRD_PROMPT["write-prd"]
            EAR_PROMPT["write-ears"]
            SETUP_PROMPT["setup-check"]
        end
    end

    %% Content Directories (Right)
    subgraph CONTENT ["📁 Content & Output"]
        direction TB

        subgraph DOCS ["📖 Documentation"]
            ADR_TEMPLATE["adr-template.md"]
            PRD_TEMPLATE["prd-template.md"]
            CODE_REVIEW["code-review-guidelines.md"]
        end

        subgraph PLANS ["📅 Plans"]
            PLAN_TEMPLATE["plan-template.md"]
            TODO["TODO.md"]
            ARCHIVE_PLAN["archived-plans"]
        end
    end

    %% Primary Configuration Flow (Core → GitHub Config)
    CI -.->|"references"| INSTRUCTIONS
    CI -.->|"references"| DOCS
    AGENTS -.->|"references"| PLANS

    %% Agent/Chat Mode Integration (GitHub Config → Content)
    DOCINST ==>|"governs"| DOCS
    BACKEND -.->|"applies to"| DOCS
    FRONTEND -.->|"applies to"| DOCS

    %% Prompt → Template Relationships
    DOC_PROMPT ==>|"generates"| DOCS
    ADR_PROMPT ==>|"generates"| ADR_TEMPLATE
    PRD_PROMPT ==>|"generates"| PRD_TEMPLATE
    SETUP_PROMPT -.->|"validates"| CONFIG

    %% Instruction → Content Rules
    DOCINST ==>|"governs"| DOCS

    %% Archive References
    ARCHIVE_PLAN -.->|"references"| CONFIG
    ARCHIVE_PLAN -.->|"uses"| PLAN_TEMPLATE

    %% Styling
    classDef config fill:#e3f2fd,stroke:#1976d2,stroke-width:3px,color:#000
    classDef agent fill:#e8f5e8,stroke:#388e3c,stroke-width:2px,color:#000
    classDef instruction fill:#e8f5e8,stroke:#388e3c,stroke-width:2px,color:#000
    classDef prompt fill:#fff3e0,stroke:#f57c00,stroke-width:2px,color:#000
    classDef content fill:#fce4ec,stroke:#c2185b,stroke-width:2px,color:#000
    classDef plan fill:#f1f8e9,stroke:#689f38,stroke-width:2px,color:#000

    class CI,README config
    class DEV,CR,TEST agent
    class DOCINST,BACKEND,FRONTEND,BDD instruction
    class DOC_PROMPT,ADR_PROMPT,PRD_PROMPT,SETUP_PROMPT prompt
    class ADR_TEMPLATE,PRD_TEMPLATE,CODE_REVIEW content
    class PLAN_TEMPLATE,TODO,ARCHIVE_PLAN plan
```

## 6. Appendix: SSOT Source Map

Authoritative single sources of truth (SSOT) for key policies and templates. Prefer linking to these instead of duplicating content.

- Core policies and workflow
  - Copilot instructions (SSOT): `.github/copilot-instructions.md`
    - Quality & Coverage Policy: `.github/copilot-instructions.md#quality-policy`

### CI Coverage Enforcement

This repo includes a minimal coverage enforcement workflow (`.github/workflows/coverage.yml`) and script (`.github/scripts/enforce-coverage.js`) aligned with the Quality & Coverage Policy:

- All code ≥ 95%; critical/hot/error/security paths 100%.
- Branching & Workflow: see "Project Methodologies" in the same file
- Naming & Commit Conventions: see corresponding sections in the same file

- Engineering guidelines
  - Code review checklist (SSOT): `docs/engineering/code-review-guidelines.md#code-review-checklist`
  - Pull request guidelines: `docs/engineering/pull-request-guidelines.md`
- Documentation
  - Docs authoring rules (SSOT): `.github/instructions/docs.instructions.md`
  - Documentation flow anchor: `.github/instructions/docs.instructions.md#documentation-process-flow`
- Testing
  - **QA Workflows (Standard):** `npm run sonar:quick` / `npm run sonar:full` (see **Quality Assurance Workflows** above)
  - Development Setup & Quality Guide: `docs/engineering/development-setup.md`
  - BDD feature guidance (SSOT): `.github/instructions/bdd-tests.instructions.md`
  - Tester agent (enforces policy): `.github/agents/Tester.agent.md`
- Backend
  - Backend instructions (SSOT): `.github/instructions/backend.instructions.md`
  - Architecture: `.github/instructions/backend.instructions.md#backend-architecture`
  - Error handling: `.github/instructions/backend.instructions.md#backend-error-handling`
  - Observability: `.github/instructions/backend.instructions.md#backend-observability`
  - Security: `.github/instructions/backend.instructions.md#backend-security`
- Planning
  - Plan template (SSOT): `plans/plan-template.md`
  - Small plan example: `plans/examples/plan-small.md`
  - TODO (work queue): `plans/TODO.md`