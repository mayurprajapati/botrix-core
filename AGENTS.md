# AI Agents Guide for `botrix-core` Project

Welcome to the `botrix-core` repository. This project serves as the foundational shared library for all Java projects in the ecosystem, acting as the "Source of Truth" for common models, utilities, and communication protocols.

As an AI agent working in this repository, you **MUST** utilize the localized skill files located in the `.agents/skills/` directory to ensure architectural consistency and adherence to project constraints.

## Available Skills & When to Use Them

### 1. `botrix_core_system_skill`
**Location:** `.agents/skills/workspace_system_skill/SKILL.md`

**When to use:**
- **Always** read this before modifying shared models, DTOs, or common utilities (like encryption or Selenium/Chrome driver management).
- When you need to understand or modify gRPC service definitions (`pygrpc`).
- When you are making changes that might affect consumer projects like `botrix-flow`.

**Key Constraints Enforced:**
- **Dependency Propagation Rule:** Because `botrix-flow` consumes this library via Maven, **ANY** change to a method signature or model structure requires a Maven re-build/install (`mvn install`) of this project *before* `botrix-flow` can successfully compile with the changes.
- **Backwards Compatibility:** Always ensure that changes to shared utilities or models are backwards compatible whenever possible to avoid breaking downstream consumers.
- **Hibernate Verification:** If changing a core model that is used in database entities, you must verify the impact on the `botrix-flow` Hibernate configuration.

## General AI Agent Constraints

1. **Skill Discovery:** Before creating new shared models or utilities, check the `.agents/skills/` folder and read the relevant `SKILL.md` files to align with the core architectural philosophy.
2. **Read Before Writing:** Treat the `SKILL.md` files as the absolute source of truth for the library's foundation.
3. **Cross-Project Awareness:** Remember that this project is a core dependency. Changes made here ripple outwards to `botrix-flow` and potentially other Java services. Always follow the AI Agent Maintenance Protocol defined in `botrix_core_system_skill`.
