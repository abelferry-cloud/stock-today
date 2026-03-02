# Stock Today - AI 驱动的股票分析平台

基于 Spring AI 和 RAG（检索增强生成）技术的智能股票分析系统。

## 项目概述

Stock Today 是一个多模块 Maven 项目，提供 AI 驱动的股票数据分析服务。系统通过 XXL-Job 定时采集股票数据，使用 RabbitMQ 进行消息队列处理，并通过 RAG 管道将数据向量化存储到 Pinecone，最终提供基于 AI 的股票分析聊天功能。

## 模块说明

### stock-common
共享库模块，包含：
- 数据库实体类（Domain）
- MyBatis Mapper 接口和 XML
- DTO/VO 数据传输对象
- 工具类（ID 生成、时间处理、数据解析等）

### stock-crawler
数据采集服务，功能包括：
- 使用 XXL-Job 进行分布式任务调度
- 采集国内大盘指数（上证指数、深证成指）
- 采集个股实时数据
- 采集国际指数（道琼斯、纳斯达克、恒生等）
- 通过 RabbitMQ 发布采集的数据

### stock-analysis-ai
AI 分析服务（Spring Boot 3.2.5），功能包括：
- RAG 管道数据处理
- 向量存储（Pinecone）
- AI 聊天接口（DeepSeek）
- 文本 Embedding（阿里云百炼）
- Knife4j API 文档

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 基础语言 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring AI | 1.0.0 | AI 集成框架 |
| XXL-Job | 2.4.0 | 分布式任务调度 |
| RabbitMQ | - | 消息队列 |
| MyBatis | - | 数据库 ORM |
| Pinecone | - | 向量数据库 |
| Knife4j | - | API 文档 |
| 通义千问 | text-embedding-v3 | 文本 Embedding（1024 维） |
| DeepSeek | deepseek-chat | AI 聊天模型 |

## 快速开始

### 前置要求

1. Java 17+
2. Maven 3.6+
3. MySQL 8.0+
4. RabbitMQ 3.x
5. XXL-Job Admin 2.4.0
6. Pinecone 账号
7. DeepSeek API Key
8. 阿里云百炼账号（DashScope）

### 本地配置

1. **克隆仓库后配置敏感信息：**

```bash
cd scripts
cp secrets.env.template secrets.env
# 编辑 secrets.env，填入真实的 API Key 和密码
```

2. **Git 过滤器配置（首次使用）：**

```bash
# 在项目根目录执行
git config filter.stocksecrets.clean "./scripts/git-filter-clean.sh"
git config filter.stocksecrets.smudge "./scripts/git-filter-smudge.sh"
git config filter.stocksecrets.required true
```

### 启动服务

#### 1. 启动 stock-crawler（数据采集）

```bash
cd stock-crawler
mvn clean package
java -jar target/stock-crawler-*.jar --spring.profiles.active=mq,xxljob,stock
```

需要配置：
- `application-mq.yml`: RabbitMQ 连接信息
- `application-xxljob.yml`: XXL-Job 执行器配置
- `application-stock.yml`: 股票数据源配置

#### 2. 启动 stock-analysis-ai（AI 分析）

```bash
cd stock-analysis-ai
mvn clean package
java -jar target/stock-analysis-ai-*.jar
```

服务运行在 8080 端口，API 文档地址：`http://localhost:8080/swagger-ui.html`

### 构建项目

```bash
# 构建整个项目
mvn clean install

# 构建单个模块
mvn clean package -pl stock-crawler -am
```

## API 接口

### stock-analysis-ai

| 接口 | 方法 | 说明 |
|------|------|------|
| `/chat` | POST | 同步 AI 聊天 |
| `/chat/stream` | POST | 流式 AI 聊天 |
| `/api/mq/status` | GET | RabbitMQ 连接状态 |
| `/api/mq/queues/info` | GET | 队列信息 |

### XXL-Job 任务（stock-crawler）

| 任务名 | 说明 |
|--------|------|
| `getInnerMarketInfo` | 采集国内大盘指数 |
| `getStockRTIndex` | 采集个股实时数据 |
| `getOuterMarketInfo` | 采集国际指数 |

## 数据流程

```
┌─────────────────┐     ┌──────────────┐     ┌──────────────────┐
│  stock-crawler  │────▶│   RabbitMQ   │────▶│  stock-analysis-ai│
│  (数据采集)     │     │  (消息队列)   │     │   (AI 分析)        │
└─────────────────┘     └──────────────┘     └──────────────────┘
                                │
                                ▼
                         ┌──────────────┐
                         │    Pinecone  │
                         │  (向量存储)   │
                         └──────────────┘
```

1. **数据采集**: stock-crawler 通过 XXL-Job 定时任务采集股票数据
2. **消息发布**: 数据发布到 RabbitMQ 队列
3. **数据消费**: stock-analysis-ai 消费队列消息
4. **RAG 处理**: 数据向量化后存储到 Pinecone
5. **AI 聊天**: 用户通过聊天接口获取股票分析结果

## 配置文件说明

| 文件 | 模块 | 说明 |
|------|------|------|
| `application.yml` | stock-analysis-ai | 主配置（AI、服务端口） |
| `application-db.yml` | stock-analysis-ai | 数据库配置 |
| `application-cache.yml` | stock-analysis-ai | 缓存配置 |
| `application-canal.yml` | stock-analysis-ai | Canal 配置 |
| `application-mq.yml` | stock-crawler | RabbitMQ 配置 |
| `application-stock.yml` | stock-crawler | 股票数据源配置 |
| `application-xxljob.yml` | stock-crawler | XXL-Job 配置 |

## 安全说明

⚠️ **重要**: 本项目使用 Git 过滤器保护敏感信息。

- `scripts/secrets.env` 存储真实 API Key 和密码，**已加入 .gitignore，永不提交**
- `scripts/secrets.env.template` 是模板文件，可安全提交
- 提交到仓库的配置文件中的敏感信息会被自动替换为占位符（如 `YOUR_DEEPSEEK_API_KEY`）
- checkout 时会自动从 `secrets.env` 还原真实值

## 许可证

本项目仅供学习和内部使用。

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
