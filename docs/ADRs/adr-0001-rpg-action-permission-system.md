---
title: "ADR-0001: Context-Aware Action Permission System"
status: "Proposed"
date: "2026-02-23"
authors: "Antigravity, Karol"
tags: ["architecture", "decision", "authz", "rpg"]
---

# ADR-0001: Context-Aware Action Permission System

## Status

**Proposed**

## Context

The "RPG Manager" application has a standard RBAC (Role-Based Access Control) mechanism based on system roles (ADMIN, GM, PLAYER). However, a much more flexible, "game-like" constraint mechanism is needed. Game Masters must be able to dynamically manage specific ingame actions (e.g., `DISTRIBUTE_POINTS`, `LEVEL_UP`) based on context (e.g., `CAMPAIGN`, `SESSION`, `DIALOGUE`). Furthermore, the system must support the introduction of exceptions for individual characters, intended for future management from the Admin Panel. The RBAC model is insufficient to describe such fine-grained, variable permissions.

## Decision

Introduction of an **Action Policies** system inspired by ABAC (Attribute-Based Access Control), but simplified and embedded in the domain of RPG games.

We will design the database and service layer to:
1. Store **Actions** as dictionary objects with a defined scope.
2. Introduce **Context** objects – defining what the rule is applied to (e.g. type:`SESSION`, id:`123`).
3. Create **Context Policies (Main Policies)** assigned permanently by the GM to an element (e.g. whole campaign blocked `LEVEL_UP`).
4. Create **Character Overrides** – overriding group policies for selected characters.

The checking mechanism (Resolve) will cascade:
`Check Global Admin` -> `Check Character Override` -> `Check Session Context` -> `Check Campaign Context` -> `Return Default Status`.

## Consequences

### Positive

- **POS-001**: GM gains a powerful tool to control mechanics at every level (entire party vs individuals).
- **POS-002**: Very high flexibility and extensibility to any RPG systems.
- **POS-003**: Meets the readiness requirements for a later administrator panel (actions are dictionaries).

### Negative

- **NEG-001**: Complexity. Every important business action (UI) must be verified by this system, not just by Spring Security Roles.
- **NEG-002**: N-plus-1 problem. The recursive evaluation mechanism (override -> session -> campaign) can cause many DB queries.

## Alternatives Considered

### RBAC + Custom Roles per Campaign
- **ALT-001**: **Description**: Creating new micro-roles in Spring Security, e.g., `CAMP_123_CAN_LEVEL_UP`.
- **ALT-002**: **Rejection Reason**: Explosion of the number of roles and difficulty in maintaining the history/exceptions of characters. Security Roles work well in access to endpoints, not micromanagement of mechanics in the domain itself.

## Implementation Notes

- **IMP-001**: Caching of the evaluation abstraction (e.g., queries about active rules to the game context) should be applied, using Spring Cache.
- **IMP-002**: We will need `ActionPermissionEvaluator`, a new domain class resolving permissions on demand. Called from the Service Layer.
