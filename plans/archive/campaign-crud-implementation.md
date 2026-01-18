# Plan: Campaign CRUD Implementation

## 1. Title
Implement Campaign CRUD Functionality

## 2. Short description
Implement full Create, Read, Update, and Delete (CRUD) functionality for Campaigns in both Backend (API) and Frontend (UI). This enables Game Masters to manage their campaigns.

## 3. Current status
- owner: AI Agent
- state: complete
- last_updated: 2026-01-17
- blockers: []

## 4. Objectives
1.  Expose RESTful API endpoints for Campaign management by 2026-01-17.
2.  Provide a Frontend UI to List, Create, Edit, and Delete Campaigns by 2026-01-17.

## 5. Success criteria
- name: API Availability
  metric: Endpoints available
  target: GET, POST, PUT, DELETE for /api/campaigns
  verification: Manual testing via curl or Postman
- name: UI Functionality
  metric: User capabilities
  target: Users can complete full lifecycle of a campaign via UI
  verification: Manual walkthrough of UI

## 6. Scope
in:
- Backend: CampaignController, CampaignService, CampaignDTO
- Frontend: CampaignService, CampaignListComponent, CampaignCreate/Edit Component
- Routing for Campaign views
out:
- Complex permission logic (RBAC) beyond basic "Game Master" assignment (initially simplified)
- Real-time updates

## 7. Stakeholders & Roles
- AI Agent — Developer — Responsible for implementation

## 8. High-level timeline & milestones
1.  M1 — Backend API Complete — 2026-01-17 — AI Agent
2.  M2 — Frontend UI Complete — 2026-01-17 — AI Agent

## 9. Task list
- T-001 | Create CampaignDTO and Mapper | AI Agent | complexity: S | dependencies: [] | done: false
- T-002 | Create CampaignService (Backend) | AI Agent | complexity: S | dependencies: [T-001] | done: false
- T-003 | Create CampaignController (Backend) | AI Agent | complexity: S | dependencies: [T-002] | done: false
- T-004 | Create Campaign Model & Service (Frontend) | AI Agent | complexity: S | dependencies: [T-003] | done: false
- T-005 | Create Campaign List Component | AI Agent | complexity: M | dependencies: [T-004] | done: false
- T-006 | Create Campaign Form Component (Create/Edit) | AI Agent | complexity: M | dependencies: [T-004] | done: false
- T-007 | Register Routes and Add Navigation | AI Agent | complexity: XS | dependencies: [T-005, T-006] | done: false

## 10. Risks and mitigations
- R-001: LazyInitializationException on Entities | probability: medium | impact: medium | mitigation: Use DTOs and explicit mapping in Service layer

## 11. Assumptions
- Database is running and accessible.
- Authentication context provides current user (Game Master).

## 12. Implementation approach
- **Backend**: Use Spring Boot best practices. Layered architecture: Controller -> Service -> Repository. Use DTOs to avoid exposing Entity internal structure and potential infinite recursion with bi-directional relationships (Campaign <-> User).
- **Frontend**: Angular feature module (or standalone components if project uses them). Use Reactive Forms for the create/edit view.

## 13. Testing & validation plan
- Unit tests for Backend Service.
- Manual verification of API endpoints.
- Browser-based verification of UI flows.

## 14. Deployment plan & roll-back strategy
- Standard application build and restart.

## 15. Monitoring & observability
- Logs from Spring Boot.

## 16. Compliance, security & privacy considerations
- Ensure only authenticated users can create campaigns.

## 17. Communication plan
- N/A

## 18. Related documents & links
- N/A
