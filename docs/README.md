[中文](zh/README.md)

# microsphere-dynamic Documentation

Bilingual documentation for [microsphere-dynamic](../README.md), the Microsphere project
that provides dynamic, per-module JDBC auto-configuration for Spring Boot (data sources,
ShardingSphere, MyBatis, MyBatis-Plus, transactions).

- **English is the default language.** Chinese content lives in a parallel `zh/` directory
  beside every document, with identical file names.
- Documentation analyses **release branches only**. One subdirectory per branch line:
  `current` for `release`, `1.x` for `release-1.x`.

> **Current state of this repository:** no `release` or `release-1.x` branch exists yet and
> there are no tags. Only the `current` line is published, generated from `main`
> (revision `0.0.1-SNAPSHOT`). The `1.x` directory will appear once a `release-1.x` branch
> exists.

## Documentation lines

| Line | Source branch | Status | User Guide | Developer Guide |
| --- | --- | --- | --- | --- |
| `current` | `main` (stands in for `release`, which does not exist yet) | Published (EN + 中文) | [current/user-guide/README.md](current/user-guide/README.md) | [current/developer-guide/README.md](current/developer-guide/README.md) |
| `1.x` | `release-1.x` | Not applicable — branch does not exist | — | — |

## What each guide contains

- [User Guide](current/user-guide/README.md) — API explanations, code examples taken from
  the project's own test cases, version information, and the compatibility range, one
  document per Maven module.
- [Developer Guide](current/developer-guide/README.md) — architecture design, source
  analysis, design patterns, Mermaid flow/sequence diagrams for complex flows, and module
  dependency diagrams, one document per Maven module.

## Modules documented (`current` line)

| Module | Packaging | User Guide | Developer Guide |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [user-guide](current/user-guide/microsphere-dynamic-jdbc-spring-boot.md) | [developer-guide](current/developer-guide/microsphere-dynamic-jdbc-spring-boot.md) |
| `microsphere-dynamic-parent` | POM | [user-guide](current/user-guide/microsphere-dynamic-parent.md) | [developer-guide](current/developer-guide/microsphere-dynamic-parent.md) |
| `microsphere-dynamic-dependencies` | POM (BOM) | [user-guide](current/user-guide/microsphere-dynamic-dependencies.md) | [developer-guide](current/developer-guide/microsphere-dynamic-dependencies.md) |

---

## 中文

中文文档入口：[zh/README.md](zh/README.md)
