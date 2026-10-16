[English](../README.md)

# 开发者指南 —— `current` 文档线

**microsphere-dynamic** 的开发者文档：架构设计、源码分析、设计模式，以及模块依赖关系与
复杂运行流程的 Mermaid 流程图。

> **文档线说明：** 本仓库目前尚无 `release` 或 `release-1.x` 分支（也没有任何 tag）；
> `current` 文档线由 `main` 分支生成（revision 为 `0.0.1-SNAPSHOT`）。请将本文档中所有
> 版本号与行为视为 1.0 之前、随时可能变动的内容。

## 模块文档

每个 Maven 模块一篇文档（reactor 中恰好声明了三个模块）：

| 模块 | 打包方式 | 文档 | 内容 |
| --- | --- | --- | --- |
| `microsphere-dynamic-jdbc-spring-boot` | JAR | [microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md) | 架构与包分层、启动 / 配置变更 / 校验流程的时序图、`META-INF/spring.factories` 中注册的 SPI 契约、逐包源码分析、带类引用的设计模式清单、新增模块的扩展指南、开发环境搭建 |
| `microsphere-dynamic-parent` | POM | [microsphere-dynamic-parent.md](microsphere-dynamic-parent.md) | parent 存在的意义、POM 逐区块分析、`${revision}` 与 flatten 机制、属性/版本的消费方、变更管理规则 |
| `microsphere-dynamic-dependencies` | POM（BOM） | [microsphere-dynamic-dependencies.md](microsphere-dynamic-dependencies.md) | BOM 作为对外受管成员面、如何扩展、reactor 排序约束 |

## 模块依赖总览

请查看各文档内的图示 —— 模块间依赖关系一律以图呈现，不用文字罗列：

- reactor / 继承链：[microsphere-dynamic-parent.md](microsphere-dynamic-parent.md#2-模块依赖图)
- 运行期包依赖：[microsphere-dynamic-jdbc-spring-boot.md](microsphere-dynamic-jdbc-spring-boot.md)

## 文档导航

- 用户指南（`current` 线）：[../../user-guide/README.md](../../user-guide/README.md)
- 文档首页（全部文档线与语言）：[../../../README.md](../../../README.md)
- 仓库根 README：[../../../../README.md](../../../../README.md)

---

## English

英文版本入口：[../README.md](../README.md)
