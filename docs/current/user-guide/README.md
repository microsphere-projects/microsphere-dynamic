[中文](zh/README.md)

# User Guide — `current` line

User documentation for **microsphere-dynamic** (Microsphere Projects for Dynamic), a Maven
reactor that provides dynamic, per-module JDBC auto-configuration for Spring Boot
applications (data sources, ShardingSphere, MyBatis, MyBatis-Plus, transactions).

> **Documentation line note:** this repository has no `release` or `release-1.x` branch yet
> (no tags either). The `current` documentation line is therefore generated from the `main`
> branch, whose revision is `0.0.1-SNAPSHOT`. Treat every version and behaviour described
> here as pre-1.0 and subject to change.

## Module documents

One document per Maven module (the reactor declares exactly three modules):

| Module | Packaging | Document | What you will find |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md) | The flagship library: quick start, configuration reference (`microsphere.dynamic.jdbc.*`), usage scenarios with examples taken from the project's own test cases, dynamic config change, API summary, version & compatibility, troubleshooting |
| `microsphere-dynamic-parent` | POM | [microsphere-dynamic-parent.md](microsphere-dynamic-parent.md) | How to use the module as a `<parent>`: pinned third-party versions (MySQL, MyBatis, MyBatis-Plus, ShardingSphere, MariaDB4j), imported Microsphere BOMs, `${revision}` scheme |
| `microsphere-dynamic-dependencies` | POM (BOM) | [microsphere-dynamic-dependencies.md](microsphere-dynamic-dependencies.md) | How to import the BOM in your own `<dependencyManagement>` and what it manages (only `microsphere-dynamic-jdbc-spring-boot` today) |

## Prerequisites

| Item | Value | Where it comes from |
| --- | --- | --- |
| JDK | 8 (baseline) | Inherited from the external parent `microsphere-build:0.0.21`, not declared in this repository |
| Spring Boot | 2.7.18 line | Inherited via the imported `microsphere-spring-cloud-dependencies` chain, not declared here |
| ShardingSphere JDBC | 5.1.1 | Declared in `microsphere-dynamic-parent` |
| MyBatis / starter | 3.5.15 / 2.3.1 | Declared in `microsphere-dynamic-parent` |
| MyBatis-Plus | 3.3.0 | Declared in `microsphere-dynamic-parent` |

## Documentation map

- Developer Guide (`current` line): [../developer-guide/README.md](../developer-guide/README.md)
- Docs home (all lines and languages): [../../README.md](../../README.md)
- Repository root README: [../../../README.md](../../../README.md)

---

## 中文

中文版本入口：[zh/README.md](zh/README.md)
