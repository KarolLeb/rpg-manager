---
description: 'Analyze CRAP (Change Risk Anti-Patterns) metrics and refactor code to lower scores.'
---

# CRAP Metric Resolution

This repository enforces a strict limit on the CRAP (Change Risk Anti-Patterns) metric. The pipeline will fail if any method in the codebase exceeds a **CRAP score of 30**. 

## What is the CRAP Score?

The CRAP score combines Cognitive/Cyclomatic Complexity with Test Coverage identifying code that is both highly complex and poorly tested.

The formula used by the pipeline is:
`CRAP(m) = comp(m)^2 * (1 - cov(m))^3 + comp(m)`
Where:
- `comp(m)` is the complexity of method `m`
- `cov(m)` is the test coverage of method `m` (as a decimal, e.g., 0.85)

## How to Resolve CRAP Violations

When an AI agent (or developer) encounters a CRAP violation, it MUST follow these steps to resolve it efficiently:

### Step 1: Analyze the Method
1. Re-read the method causing the violation.
2. Determine if the high score is due to zero/low test coverage or extreme cognitive complexity.

### Step 2: Extract and Refactor (If Highly Complex)
If the method has many branches (`if`, `else`, `switch`, loops):
- **Extract logic**: Break the large method down into smaller, focused helper methods or classes with single responsibilities.
- By splitting the complexity, the CRAP score of individual components drops dramatically, even before new tests are written.

### Step 3: Add Missing Tests
If the method already has low complexity but still fails the CRAP check, it likely lacks test coverage.
- Write unit tests targeting the missing execution branches.
- Use mocks appropriately if the untested logic interacts with external services or complex state.

### Step 4: Verify Locally
Before committing or claiming the issue is resolved, verify the fix:
- Backend: Run `mvn clean verify` which generates the Jacoco XML and natively invokes the `.github/scripts/crap-analyzer.js` script to assert the new scores.

<!-- © Capgemini 2026 -->
