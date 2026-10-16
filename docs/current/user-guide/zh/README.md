[English](../README.md)

# 用户指南 —— `current` 文档线

**microsphere-dynamic**（Microsphere Projects for Dynamic）的用户文档。该 Maven reactor
为 Spring Boot 应用提供按模块隔离的动态 JDBC 自动配置能力（数据源、ShardingSphere、
MyBatis、MyBatis-Plus、事务）。

> **文档线说明：** 本仓库目前尚无 `release` 或 `release-1.x` 分支（也没有任何 tag）；
> `current` 文档线由 `main` 分支生成（revision 为 `0.0.1-SNAPSHOT`）。请将本文档中所有
> 版本号与行为视为 1.0 之前、随时可能变动的内容。

## 模块文档

每个 Maven 模块一篇文档（reactor 中恰好声明了三个模块）：

| 模块 | 打包方式 | 文档 | 内容 |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md) | 旗舰库：快速开始、配置参考（`microsphere.dynamic.jdbc.*`）、取材于工程自身测试用例的使用场景、动态配置变更、API 汇总、版本与兼容性、故障排查 |
| `microsphere-dynamic-parent` | POM | [microsphere-dynamic-parent.md](microsphere-dynamic-parent.md) | 如何将其作为 `<parent>` 使用：第三方版本锁定（MySQL、MyBatis、MyBatis-Plus、ShardingSphere、MariaDB4j）、导入的 Microsphere BOM、`${revision}` 机制 |
| `microsphere-dynamic-dependencies` | POM（BOM） | [microsphere-dynamic-dependencies.md](microsphere-dynamic-dependencies.md) | 如何在自己的 `<dependencyManagement>` 中导入该 BOM，以及它当前管理的内容（目前仅 `microsphere-dynamic-jdbc-spring-boot`） |

## 前置条件

| 项目 | 取值 | 来源 |
| --- | --- | --- |
| JDK | 8（基线） | 继承自外部父 POM `microsphere-build:0.0.21`，并非本仓库声明 |
| Spring Boot | 2.7.18 系列 | 经由导入的 `microsphere-spring-cloud-dependencies` 链继承，并非本仓库声明 |
| ShardingSphere JDBC | 5.1.1 | 在 `microsphere-dynamic-parent` 中声明 |
| MyBatis / starter | 3.5.15 / 2.3.1 | 在 `microsphere-dynamic-parent` 中声明 |
| MyBatis-Plus | 3.3.0 | 在 `microsphere-dynamic-parent` 中声明 |

## 文档导航

- 开发者指南（`current` 线）：[../../developer-guide/README.md](../../developer-guide/README.md)
- 文档首页（全部文档线与语言）：[../../../README.md](../../../README.md)
- 仓库根 README：[../../../../README.md](../../../../README.md)

---

## English

英文版本入口：[../README.md](../README.md)
