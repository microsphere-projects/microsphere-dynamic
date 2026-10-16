[English](../README.md)

# microsphere-dynamic 文档

[microsphere-dynamic](../../README.md) 的双语文档。该 Microsphere 工程为 Spring Boot 提供
按模块隔离的动态 JDBC 自动配置能力（数据源、ShardingSphere、MyBatis、MyBatis-Plus、事务）。

- **英文为默认语言**，主路径即英文版；中文内容与英文一一对应，存放在每篇文档旁边的 `zh/`
  目录中，文件名完全相同。
- 文档**仅分析 release 分支**：每条分支线一个子目录，`release` 对应 `current`，
  `release-1.x` 对应 `1.x`。

> **本仓库现状：** 目前尚无 `release` 与 `release-1.x` 分支，也没有任何 tag；因此只发布了
> `current` 一条文档线，由 `main` 分支生成（revision 为 `0.0.1-SNAPSHOT`）。待 `release-1.x`
> 分支建立后，`1.x` 目录才会出现。

## 文档线

| 文档线 | 来源分支 | 状态 | 用户指南 | 开发者指南 |
| --- | --- | --- | --- | --- |
| `current` | `main`（暂代尚不存在的 `release`） | 已发布（EN + 中文） | [current/user-guide/README.md](../current/user-guide/zh/README.md) | [current/developer-guide/README.md](../current/developer-guide/zh/README.md) |
| `1.x` | `release-1.x` | 不适用 —— 该分支不存在 | — | — |

## 两类文档的内容

- [用户指南](../current/user-guide/zh/README.md) —— API 解释说明、取材于工程自身测试用例的代码示例、
  版本信息以及版本兼容范围，每个 Maven 模块一篇。
- [开发者指南](../current/developer-guide/zh/README.md) —— 架构设计、源码分析、设计模式，复杂流程以
  Mermaid 流程图/时序图呈现，模块间依赖关系以图示表达，每个 Maven 模块一篇。

## 已覆盖模块（`current` 线）

| 模块 | 打包方式 | 用户指南 | 开发者指南 |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [user-guide](../current/user-guide/zh/microsphere-dynamic-jdbc-spring-boot.md) | [developer-guide](../current/developer-guide/zh/microsphere-dynamic-jdbc-spring-boot.md) |
| `microsphere-dynamic-parent` | POM | [user-guide](../current/user-guide/zh/microsphere-dynamic-parent.md) | [developer-guide](../current/developer-guide/zh/microsphere-dynamic-parent.md) |
| `microsphere-dynamic-dependencies` | POM（BOM） | [user-guide](../current/user-guide/zh/microsphere-dynamic-dependencies.md) | [developer-guide](../current/developer-guide/zh/microsphere-dynamic-dependencies.md) |

---

## English

English entry point: [../README.md](../README.md)
