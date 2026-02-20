# APIKeyManager 功能说明

## 概述

在 `stock-analysis-ai` 模块中新增 API Key 管理器，支持多 Key 负载均衡和 429 限流自动冷却。

## 新增文件

| 文件路径 | 说明 |
|---------|------|
| `config/ApiKeyProperties.java` | 配置属性类，支持多 Key 配置 |
| `config/ApiKeyManager.java` | 核心管理器，实现轮询和冷却逻辑 |

## 修改文件

| 文件路径 | 说明 |
|---------|------|
| `config/AIConfig.java` | 初始化 ApiKeyManager |
| `service/impl/ChatServiceImpl.java` | 检测 429 错误并标记冷却 |
| `resources/application.yml` | 配置多个 API Key |

## 功能特性

1. **负载均衡**：Round Robin 轮询算法分配请求
2. **线程安全**：使用 AtomicInteger 保证高并发正确性
3. **429 冷却**：指数退避策略（1min → 2min → 4min → 8min，最大 10min）
4. **向后兼容**：支持单 Key 和多 Key 配置

## 配置示例

```yaml
spring:
  ai:
    deepseek:
      api-keys:
        - sk-key1
        - sk-key2
        - sk-key3
```