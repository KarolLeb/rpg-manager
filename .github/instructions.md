# RPG Manager - Project Instructions

## Overview
**RPG Manager** is a system for managing RPG sessions, consisting of a Java (Spring Boot) backend and an Angular frontend.

## Detailed Instructions
Please refer to the specific instruction files for detailed guidelines:

- **[Backend Development](instructions/backend.instructions.md)**: Tech stack, architecture, entities, and workflow.
- **[Frontend Development](instructions/frontend.instructions.md)**: Angular framework, structure, and state management.
- **[Infrastructure & Docker](instructions/infrastructure.instructions.md)**: Docker Compose setup, ports, and troubleshooting.
- **[Documentation Standards](instructions/docs.instructions.md)**: Guidelines for writing and maintaining project documentation.
- **[BDD Testing](instructions/bdd-tests.instructions.md)**: Guidelines for Behavior-Driven Development tests.

## Core Principles
1.  **Strict Worktree Usage:** NEVER work directly on the `master` branch. You MUST create a dedicated git worktree and a new branch (e.g., `feature/...`, `fix/...`) from the latest `master` BEFORE executing any commands that modify the codebase. Keep the `master` branch clean at all times. Make small, frequent commits on your feature branch rather than large, monolithic ones.
2.  **Test-Driven Development (TDD):** Write tests for *all* new behaviors **before** implementing the behavior itself. No implementation code without a failing test.
3.  **Permission First:** Never commit or push changes without explicit user permission. Always propose the commit first.
4.  **Complexity Check:** When modifying a function/method, check its Cyclomatic Complexity. If it exceeds **5**, report it immediately and consider refactoring.
5.  **Plan Before Action:** Always report a clear plan before starting implementation.
6.  **Context First:** Always understand the surrounding code and architecture before making changes.
7.  **Consistency:** Adhere strictly to the coding styles and patterns defined in the specific instruction files.
8.  **Safety:** Never commit secrets, passwords, or API keys.
9.  **Environment Awareness:** This agent runs in a **PowerShell** environment on Windows. All shell commands, scripts, and file system operations must use PowerShell-compatible syntax and follow Windows-specific path conventions where applicable.
10. **Efficient Testing:** When fixing or updating tests, only rerun the relevant test classes or modules to ensure fast feedback cycles. Run the full test suite only after local changes are verified.
11. **Documentation Updates:** Always update relevant documentation, requirements, and agent behavior rules immediately after every successful task implementation.