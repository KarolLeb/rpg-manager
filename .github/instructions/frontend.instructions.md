<!--
SECTION PURPOSE: Frontmatter defines scope (which files are governed by these rules).
PROMPTING: Keep the YAML minimal; AI should respect this glob when proposing edits.
-->
---
applyTo: "**/*.js, **/*.ts, **/*.tsx"
---

<!--
SECTION PURPOSE: Introduce mandatory frontend guidance.
PROMPTING: Clear headings; concise bullets for scanability.
COMPLIANCE: Treat rules below as defaults unless project overrides exist.
-->
# Frontend Instructions

<CRITICAL_REQUIREMENT type="MANDATORY">
- Use TypeScript for new code and interfaces for component props.
- Enforce accessibility: semantic HTML first; ARIA complements, not substitutes.
- Include tests for new components and changed logic (render + key behavior).
</CRITICAL_REQUIREMENT>

<!--
SECTION PURPOSE: Universal rules for all frontend code.
PROMPTING: Imperative checklist for quick verification.
-->
## General Guidelines

1. **Code Structure**: Prefer small, reusable components and feature modules.
2. **Styling**: Follow repo standard (CSS Modules/Tailwind/SCSS). Avoid inline styles except small dynamic cases.
3. **Accessibility**: Prefer native controls, clear labels, visible focus, sufficient contrast.
4. **Testing**: Use the project's testing stack (e.g., Jest + Testing Library). See the Testing section below.

<!--
SECTION PURPOSE: Expectations when authoring components.
PROMPTING: Specify contract (props/state), error modes, and data flow norms.
-->
## Component Development

1. **Props**: Define with TypeScript; document optional vs required. Provide sensible defaults.
2. **State Management**: Prefer local state; lift to Context/Redux only when shared or cross-cutting.
3. **API Calls**: Use the shared API client; centralize endpoints and schemas; handle errors explicitly.
4. **Error Boundaries**: Add boundaries around risky trees; fail gracefully.

<!--
SECTION PURPOSE: Make testing guidance explicit and link to SSOTs (Tester chat mode and BDD instructions).
PROMPTING: Reference, don't duplicate. Keep actions concrete for frontend.
-->
## Testing

1. **SSOT References**
	- Tester chat mode: `.github/chatmodes/Tester.chatmode.md`
	- BDD tests instructions: `.github/instructions/bdd-tests.instructions.md`

2. **Unit/UI Tests (default stack: Jest + Testing Library unless overridden)**
	- Cover rendering, critical interactions (click, type, submit), and state transitions.
	- Include accessibility assertions (roles/labels/name, focus management, keyboard nav).
	- Assert async states: loading, success, and error paths; handle empty data gracefully.

3. **E2E/UI Flows (optional, if project uses Playwright/Cypress)**
	- **IMPORTANT**: Before running E2E tests, ensure the frontend container is stopped (e.g., `docker-compose down` in `docker/`) as Playwright starts its own dev server.
	- Keep scenarios small and stable; tag appropriately (e.g., `@ui`, `@smoke`).
	- Prefer testids sparingly; select by role/name first.

4. **Mutation Testing (Stryker Mutator)**
	- Use Stryker to verify test effectiveness for complex business logic and shared utilities.
	- **Targets**: >= 80% (Global/UI), >= 90% (Domain/Utils), 100% (Security/Critical).
	- Run limited mutation tests on changed files using: `npx stryker run --mutate src/path/to/file.ts`.

6. **Efficiency**
	- When fixing or updating tests, only rerun relevant test classes or modules (e.g., `npx jest src/path/to/test.spec.ts`) to ensure fast feedback cycles.
	- Run the full test suite only after local changes are verified.

<!--
SECTION PURPOSE: Keep apps fast and responsive.
PROMPTING: Short, actionable techniques.
-->
## Performance Optimization

1. **Lazy Loading**: Defer large routes and heavy components.
2. **Memoization**: Use React.memo/useMemo/useCallback to avoid unnecessary work.
3. **Code Splitting**: Split at route and major component boundaries.
4. **Minimize Re-renders**: Keep props stable; use selectors and derived memoized data.

<!--
SECTION PURPOSE: Enforce baseline quality gates.
PROMPTING: XML block for machine-checkable rules.
-->

<PROCESS_REQUIREMENTS type="MANDATORY">
- Run lints and tests locally before PR.
- Include accessibility checks (labels, keyboard nav, focus order) in reviews.
- Avoid `any`; if unavoidable, annotate with a TODO and reason.
</PROCESS_REQUIREMENTS>

<!-- © Capgemini 2025 -->
