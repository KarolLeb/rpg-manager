# PRD - RPG Action System and Contextual Permissions

## Product Requirements Document (PRD)

### 0. Changelog

| Date       | Author      | Description of changes |
| ---------- | ----------- | ---------------------- |
| 2026-02-23 | Antigravity | Initial version        |

### 1. Overview

- **Problem Statement:** Different RPG systems have different sets of actions (e.g., level up, distribute points). The Game Master (GM) needs to control when and what actions players can perform (e.g., only during an active session, only at the beginning of a campaign). The system lacks a flexible mechanism to verify permissions for specific ingame actions based on context and exceptions.
- **Value Proposition:** Enables flexible adaptation of the mechanics of any RPG. Provides GMs with full control over the course of sessions and campaigns, and in the future will allow administrators to easily manage templates for various RPG systems.

### 2. Goals & Objectives

**Goals:**
- Build a mechanism (rule/policy engine) that checks whether a given character can perform a specific action at a given moment (Campaign, Session).
- Provide the ability to create exceptions for specific characters.
- Prepare the architecture for a future Admin Panel for managing actions globally.

**Measurable Objectives:**
- Implement the system on the backend (API for checking and setting permissions) with 100% code coverage of key logic by unit tests.
- Enable permission checking in < 50ms so as not to slow down gameplay.

### 3. Stakeholders

| Group                  | Role / Responsibility | 
| ---------------------- | --------------------- | 
| Game Master (GM)       | Setting rules for their campaigns and sessions. |
| Players                | Performing actions according to granted permissions for their characters. |
| Administrator          | Global management of action types (in the future). | 

### 4. Specification and Use Cases

The system extends the current RBAC (Role-Based Access Control) model with a mechanism based on context and attributes (a preview of ABAC - Attribute-Based Access Control / Policy Engine).

- **Context:** The execution environment of the action (e.g., `CAMPAIGN`, `SESSION`).
- **Action:** A definable event (e.g., `DISTRIBUTE_POINTS`, `LEVEL_UP`).
- **Policy:** Permission or prohibition to perform an *Action* in a *Context*.
- **Exception (Override):** An individual permission granted to a specific *Character* in a given *Context*, overriding the *Policy*.

**Use Case:**
1. GM enables the possibility of `DISTRIBUTE_POINTS` only during a specific *Session*. 
2. Outside of this Session, no Character can distribute points within the Campaign.
3. GM sets an exception: Character X can `LEVEL_UP` outside the Session, because they missed the previous one.

### 5. Functional Requirements

| ID   | Feature | Role | Priority |
| ---- | ------- | ---- | -------- |
| F01  | Defining a dictionary of available Actions for a given RPG system. | ADMIN | MUST |
| F02  | Assigning default Policies for Actions in the Campaign Context. | GM | MUST |
| F03  | Overriding Action Policies for the duration of a specific Session. | GM | MUST |
| F04  | Defining Exceptions (Overrides) for a single Character for a given Action. | GM | MUST |
| F05  | Verification (API Endpoint) whether a given Character can perform an Action in a given Context. | SYSTEM | MUST |

### 6. Out of Scope
- A full-fledged graphical panel (UI) for the Administrator to manage the full action tree in the first iteration (will be done later, now only API).

### 7. Non-Functional Requirements
- **Performance:** Checking permissions `canPerformAction(characterId, actionId, contextId)` must be highly optimized or cached.
- **Extensibility:** Ability to easily add new "Contexts" in the future (e.g., "Dialogue", "Combat").

### 13. References
- ADR-0001: Context-Aware Action Permission System (attached)
