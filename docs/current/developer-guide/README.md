[中文](zh/README.md)

# Developer Guide — `current` line

Developer documentation for **microsphere-dynamic**: architecture design, source analysis,
design patterns, and Mermaid diagrams for the module dependency graph and the complex
runtime flows.

> **Documentation line note:** this repository has no `release` or `release-1.x` branch yet
> (no tags either). The `current` documentation line is therefore generated from the `main`
> branch, whose revision is `0.0.1-SNAPSHOT`. Treat every version and behaviour described
> here as pre-1.0 and subject to change.

## Module documents

One document per Maven module (the reactor declares exactly three modules):

| Module | Packaging | Document | What you will find |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md) | Architecture and package layering, bootstrap / startup / config-change / validation flows as sequence diagrams, SPI contracts registered in `META-INF/spring.factories`, per-package source analysis, design-pattern inventory with class citations, extension guide for adding a new module, development setup |
| `microsphere-dynamic-parent` | POM | [microsphere-dynamic-parent.md](microsphere-dynamic-parent.md) | Why the parent exists, section-by-section POM analysis, `${revision}` + flatten mechanism, property/version consumers, change-management rules |
| `microsphere-dynamic-dependencies` | POM (BOM) | [microsphere-dynamic-dependencies.md](microsphere-dynamic-dependencies.md) | The BOM as the public managed-member surface, how to extend it, reactor ordering constraints |

## Module dependency overview

See the diagrams inside the documents — module dependency relationships are always drawn,
never listed as prose:

- Reactor / parent chain: [microsphere-dynamic-parent.md](microsphere-dynamic-parent.md#2-module-dependency-diagram)
- Runtime package dependencies: [microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md)

## Documentation map

- User Guide (`current` line): [../user-guide/README.md](../user-guide/README.md)
- Docs home (all lines and languages): [../../README.md](../../README.md)
- Repository root README: [../../../README.md](../../../README.md)

---

## 中文

中文版本入口：[zh/README.md](zh/README.md)
