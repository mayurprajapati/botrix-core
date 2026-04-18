---
name: botrix_core_system_skill
description: >
  Master architectural documentation for the botrix-core project.
  Defines role as the shared library foundation for all Java-based botrix services.
---

# Project Role: Shared Core Library

`botrix-core` serves as the foundational shared library for all Java projects in the ecosystem. It contains the "Source of Truth" for common models, encryption/security utilities, and communication protocols.

## 🏗️ Connections & Dependencies

- **Main Consumer**: `botrix-flow` consumes this library via Maven (groupId: `botrix`, artifactId: `core`).
- **Functionality**:
  - Common DTOs and database-agnostic models.
  - Selenium/Chrome driver management utilities used for scraping.
  - gRPC service definitions (`pygrpc`) for potential Python-Java bridge communications.

## ⚠️ Cross-Project Warning: Dependency Propagation
Because `botrix-flow` depends on specific versions of `botrix-core`, changing a method signature or a model structure here **requires a Maven re-build/install** (`mvn install`) of this project before `botrix-flow` can successfully compile with the changes.

## 🤖 AI Agent Maintenance Protocol
Whenever a shared utility or model is modified:
1. Ensure the Change is backwards compatible where possible.
2. If changing a core model used in DB entities, verify the impact on the `botrix-flow` Hibernate configuration.
