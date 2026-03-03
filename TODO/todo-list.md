# 智能股票分析系统 - TODO 清单

> 本文档记录项目后续可扩展的模块和功能，侧重后端技术实现

---

## P0 - 高优先级（核心基础）

### 1. stock-user 用户中心模块

**状态**: ✅ 已完成（2026-03-03）

**功能描述**：基于现有 `SysUser`、`SysRole`、`SysPermission` 实体，实现完整的用户认证授权体系。

**实现内容**：
- [x] 用户注册/登录/登出
- [x] JWT Token 生成与验证
- [x] RBAC 权限控制（角色 - 权限 - 菜单）
- [x] 密码加密存储（BCrypt）
- [x] 登录日志记录
- [x] Token 刷新机制

**技术栈**：
- Spring Security 6.x
- JWT (io.jsonwebtoken 0.12.6)
- Redis（Token 黑名单/会话管理）
- MyBatis（已有 Mapper）

**关键实现**：
```
1. 创建 stock-user 模块（新 Maven 子模块）- 已完成
2. 实现 JwtTokenProvider 工具类 - 已完成
3. 自定义 UserDetailsService 加载用户权限 - 已完成
4. @PreAuthorize 注解控制接口权限 - 已完成
5. Redis 存储 Token 黑名单（Logout 场景）- 已实现框架，需 Redis 环境
```

**已创建文件**：
- `stock-user/pom.xml` - 模块依赖配置
- `stock-user/src/main/java/com/me/stock/user/StockUserApplication.java` - 启动类
- `stock-user/src/main/java/com/me/stock/user/config/` - 配置类（SecurityConfig, JwtProperties, RedisConfig, AsyncConfig）
- `stock-user/src/main/java/com/me/stock/user/security/` - 安全类（JwtTokenProvider, JwtAuthenticationFilter, 等）
- `stock-user/src/main/java/com/me/stock/user/service/` - 服务层（UserService, UserDetailsServiceImpl, LoginLogService）
- `stock-user/src/main/java/com/me/stock/user/controller/` - 控制器（AuthController, UserController, RoleController, PermissionController）
- `stock-user/src/main/java/com/me/stock/user/dto/` - 数据传输对象
- `stock-user/src/main/java/com/me/stock/user/common/` - 统一返回类
- `stock-user/src/main/java/com/me/stock/user/entity/SysLoginLog.java` - 登录日志实体
- `stock-user/src/main/java/com/me/stock/user/mapper/SysLoginLogMapper.java` - 登录日志 Mapper
- `stock-user/src/main/resources/mapper/SysLoginLogMapper.xml` - Mapper XML
- `stock-user/src/main/resources/sql/init-admin.sql` - 初始化 SQL 脚本

**API 接口**：
- 认证接口：`POST /auth/login`, `POST /auth/register`, `POST /auth/logout`, `POST /auth/refresh`
- 用户管理：`GET /user/current`, `GET /user/{id}`, `GET /user/list`, `PUT /user`, `PUT /user/password`
- 角色管理：`GET /role/list`, `POST /role`, `PUT /role`, `DELETE /role/{id}`, `POST /role/assign`
- 权限管理：`GET /permission/list`, `POST /permission`, `PUT /permission`, `DELETE /permission/{id}`, `POST /permission/assign`

**默认账户**：
- 管理员：`admin / admin123`
- 普通用户：`user / admin123`

---

### 1.5 模块拆分 - Chat 功能独立化

**状态**: ✅ 已完成（2026-03-03）

**功能描述**：将 `stock-analysis-ai` 模块中的非 AI 功能（如股票数据查询接口）剥离出来，创建独立的 `stock-chat` 模块。

**实现内容**：
- [x] 创建 `stock-chat` 模块
- [x] 迁移 `StockController`、`StockDataService` 到 `stock-chat`
- [x] 迁移 `StockQueryTool` 到 `stock-chat`
- [x] 配置 Redis 连接
- [x] 实现 Caffeine 本地缓存
- [x] 配置多级缓存管理器
- [x] 更新 `stock-analysis-ai` 保留纯 AI 功能

**架构说明**：
- **stock-analysis-ai**: 专注于 AI 分析功能（RAG、向量存储、AI 模型集成、StockQueryTool 作为 AI 工具）
- **stock-chat**: 负责非 AI 的股票数据查询、K 线数据、聊天功能（直接查询数据库 + 多级缓存）

**已创建文件**：
- `stock-chat/pom.xml` - 模块依赖配置
- `stock-chat/src/main/java/com/stock/chat/StockChatApplication.java` - 启动类
- `stock-chat/src/main/java/com/stock/chat/config/` - 配置类（RedisConfig, MultiLevelCacheConfig, RateLimiterConfig）
- `stock-chat/src/main/java/com/stock/chat/controller/StockController.java` - 股票数据查询接口
- `stock-chat/src/main/java/com/stock/chat/service/StockDataService.java` - 数据服务
- `stock-chat/src/main/java/com/stock/chat/common/` - 统一返回类
- `stock-chat/src/main/java/com/stock/chat/exception/GlobalExceptionHandler.java` - 全局异常处理

**stock-analysis-ai 保留的核心 AI 功能**：
- `ChatController` + `ChatServiceImpl`（使用 Spring AI + DeepSeek）
- `VectorStoreService` + `VectorStoreServiceImpl`
- `KnowledgeBaseService` + `KnowledgeBaseServiceImpl`
- `StockDataConsumer` + `StockDataConsumerImpl`
- `StockQueryTool`（作为 AI 工具函数，供 Spring AI 调用）
- AI 相关配置类（AIConfig, VectorStoreInitializer, RabbitMQConfig 等）

---

### 2. stock-alert 智能预警模块

**功能描述**：实时监控股价和技术指标，触发预警时通过多种渠道通知用户。

**实现内容**：
- 股价阈值预警（突破/跌破设定价格）
- 涨跌幅预警（日涨跌幅超过设定百分比）
- 技术指标预警（MACD 金叉/死叉、KDJ 超买/超卖）
- 预警通知渠道：WebSocket 推送、邮件、短信
- 预警历史记录查询

**技术栈**：
- 规则引擎：LiteFlow（轻量级流程编排）或 Drools
- WebSocket（实时推送）
- Spring Event（事件驱动）
- Redis（预警规则缓存）
- 邮件：Spring Boot Mail
- 短信：阿里云 SMS / 腾讯云 SMS

**关键实现**：
```
1. 预警规则表设计（alert_rule）
2. 规则引擎配置：每种预警类型 = 一个规则节点
3. 定时任务每分钟拉取股价 → 触发规则引擎 → 输出预警事件
4. 预警事件发布 → 多渠道通知消费者并行处理
5. WebSocket 主动推送至前端
```

---

### 3. Redis 缓存层

**状态**: ✅ 已完成（2026-03-03）

**功能描述**：为高频读取的数据建立多级缓存，降低数据库压力。

**实现内容**：
- [x] 引入 Redis 依赖
- [x] 配置 RedisConfig
- [x] 实现 Caffeine 本地缓存
- [x] 配置多级缓存管理器
- [x] 实时股价缓存（Key: `stock:rt:{code}`，TTL=60s）
- [x] 用户自选股列表缓存（Key: `user:watchlist:{userId}`）
- [x] RAG 查询结果缓存（相似问题答案复用）
- [x] 缓存穿透/击穿/雪崩防护
- [x] Canal 监听 binlog 主动失效缓存

**技术栈**：
- Spring Cache 抽象
- Redis
- Caffeine（本地缓存）
- Canal（binlog 监听）
- Guava 布隆过滤器
- Redisson（分布式锁）

**关键实现**：
```
1. 引入 spring-boot-starter-cache + redis + caffeine - 已完成
2. @Cacheable 注解标记缓存方法 - 已完成（StockDataServiceImpl）
3. 自定义 CacheManager 支持多级缓存 - 已完成（MultiLevelCacheConfig）
4. 缓存预热：服务启动时加载热点数据 - 待实现
5. Canal 监听 binlog → 主动失效缓存（保证一致性）- 已完成
```

**已实现位置**：
- `stock-chat/src/main/java/com/stock/chat/config/RedisConfig.java`
- `stock-chat/src/main/java/com/stock/chat/config/MultiLevelCacheConfig.java`
- `stock-chat/src/main/java/com/stock/chat/config/CanalConfig.java` - Canal 客户端配置
- `stock-chat/src/main/java/com/stock/chat/util/CacheUtils.java` - 缓存工具类（穿透/击穿/雪崩防护）
- `stock-chat/src/main/java/com/stock/chat/service/impl/StockDataServiceImpl.java` - 使用 @Cacheable 注解

**缓存防护详解**：
| 问题 | 防护方案 | 实现方式 |
|------|----------|----------|
| 缓存穿透 | 布隆过滤器 + 空值缓存 | `CacheUtils.mayExistInBloomFilter()` + `REDIS_NULL_VALUE` |
| 缓存击穿 | 互斥锁（Lua 脚本） | `CacheUtils.tryLock()` + `CacheUtils.unlock()` |
| 缓存雪崩 | 随机 TTL | `CacheUtils.setWithRandomTtl()` |

**Canal 缓存失效流程**：
```
MySQL binlog → Canal Server → Canal Client → RedisTemplate.delete() → 缓存失效
```

**已创建文件**：
- `stock-chat/src/main/java/com/stock/chat/config/CacheProperties.java` - 缓存配置属性
- `stock-chat/src/main/java/com/stock/chat/config/CanalConfig.java` - Canal 客户端
- `stock-chat/src/main/java/com/stock/chat/util/CacheUtils.java` - 缓存工具类

---

### 4. WebSocket 实时推送模块

**功能描述**：建立服务端到客户端的长连接，实现数据主动推送。

**实现内容**：
- 股价实时推送（订阅制：用户订阅关注的股票）
- 预警消息推送
- AI 分析结果流式推送（SSE 替代方案）
- 在线用户管理

**技术栈**：
- Spring WebSocket
- STOMP 协议（消息子协议）
- Redis（集群 Session 共享）
- SockJS（降级兼容）

**关键实现**：
```
1. 配置 WebSocket 端点：/ws endpoint
2. STOMP 主题设计：
   - /topic/stock/{code} 股价推送
   - /topic/alert/{userId} 预警推送
3. 用户连接时订阅关注的股票代码
4. 股价更新 → 发布到对应主题 → WebSocket 推送
```

---

## P1 - 中优先级（业务增强）

### 5. stock-kline K 线数据服务

**功能描述**：提供 K 线数据聚合和技术指标计算能力。

**实现内容**：
- K 线周期：1 分钟、5 分钟、15 分钟、30 分钟、60 分钟、日线、周线、月线
- 技术指标计算：MA、EMA、BOLL、MACD、RSI、KDJ
- K 线形态识别：头肩顶、双底、三角形、旗形
- K 线数据 REST API

**技术栈**：
- 时间窗口聚合（Spring Task / XXL-Job）
- TA-Lib（技术分析库，Java 绑定）或 自研指标计算
- MySQL（存储 K 线快照）
- Redis（缓存最新 K 线）

**关键实现**：
```
1. K 线数据表：stock_kline (code, period, open, high, low, close, volume, timestamp)
2. 定时任务：每分钟聚合 1 分钟 K 线；每天聚合日线
3. 指标计算服务：输入 K 线列表 → 输出指标值
4. 形态识别：滑动窗口匹配预设形态模板
```

---

### 6. stock-finance 财务数据模块

**功能描述**：采集和存储上市公司财务数据，提供基本面分析能力。

**实现内容**：
- 三大报表采集：资产负债表、利润表、现金流量表
- 财务指标计算：PE、PB、ROE、毛利率、净利率、资产负债率
- 杜邦分析
- 财务数据可视化 API

**技术栈**：
- HTTP 爬虫（HttpClient / WebFlux）
- 数据源：东方财富 API、同花顺 API
- 定时任务（XXL-Job）
- 数据清洗：Stream API

**关键实现**：
```
1. 财务数据表设计（按季度存储）
2. 爬虫服务：定期拉取财报数据
3. 指标计算服务：基于原始财务数据派生指标
4. 基本面评分：综合多个指标给出股票评分
```

---

### 7. stock-news 舆情分析模块

**功能描述**：采集财经新闻，利用 AI 进行情感分析，判断利好/利空。

**实现内容**：
- 财经新闻爬取（雪球、东方财富、新浪财经）
- 新闻情感分析（正面/负面/中性）
- 关键词提取（涉及哪些股票、行业）
- 热点新闻聚合
- 舆情热度与股价关联分析

**技术栈**：
- 爬虫：WebMagic / Jsoup
- NLP：HanLP（中文分词 + 情感分析）或 调用 AI API
- Elasticsearch（新闻全文检索）
- 定时任务

**关键实现**：
```
1. 新闻表：news (title, content, source, sentiment_score, stock_codes, publish_time)
2. 爬虫定时任务：每 10 分钟拉取最新新闻
3. 情感分析 Pipeline：分词 → 实体识别 → 情感打分
4. 股票关联：从新闻中提取股票代码
5. 舆情指数：某股票相关情感分值的时间序列
```

---

### 8. stock-backtest 策略回测模块

**功能描述**：提供交易策略回测框架，评估策略历史表现。

**实现内容**：
- 策略接口定义（用户可实现自定义策略）
- 历史数据回放（支持复权）
- 交易模拟（买入/卖出/持仓）
- 绩效分析：累计收益率、年化收益率、夏普比率、最大回撤、胜率
- 回测报告生成（PDF/HTML）

**技术栈**：
- 策略模式 + 模板方法模式
- 事件驱动架构
- JFreeChart（资金曲线图）
- iText（PDF 报告生成）
- 脚本引擎：Groovy（支持动态策略）

**关键实现**：
```
1. 策略接口：Strategy { onBar(BarData); onTick(TickData); }
2. 回测引擎：按时间顺序回放历史数据 → 调用策略回调
3. 交易撮合：模拟成交（下一个 K 线开盘价成交）
4. 绩效计算：遍历每日持仓和交易记录
5. 报告生成：汇总指标 + 图表
```

---

## P2 - 低优先级（高级功能）

### 9. stock-monitor 监控告警模块

**功能描述**：监控服务运行状态和业务指标。

**实现内容**：
- 服务健康检查（/actuator/health）
- 慢 SQL 监控（MyBatis 拦截器）
- 接口响应时间统计
- 业务指标：爬虫成功率、AI 响应时间、RabbitMQ 堆积量
- 告警通知（钉钉/企业微信 webhook）

**技术栈**：
- Spring Boot Actuator
- Prometheus + Grafana（指标采集 + 可视化）
- Micrometer（指标收集）
- 自定义 AOP（接口耗时统计）

**关键实现**：
```
1. 引入 actuator + micrometer-registry-prometheus
2. 自定义 MeterBinder 注册业务指标
3. Grafana 配置 Dashboard
4. Prometheus AlertManager 配置告警规则
```

---

### 10. AI 策略推荐模块

**功能描述**：基于 AI 和历史数据，提供智能选股和买卖建议。

**实现内容**：
- 智能选股：多因子筛选（技术面 + 基本面 + 舆情）
- 买卖时机建议
- 仓位管理建议
- 风险等级评估
- 推荐结果解释（AI 生成推荐理由）

**技术栈**：
- Spring AI（现有 DeepSeek 集成）
- 特征工程：历史数据派生特征
- 规则引擎 + AI 结合
- 向量数据库（Pinecone 已有）

**关键实现**：
```
1. 特征计算服务：为每只股票计算技术/基本面/舆情特征
2. 筛选服务：基于规则初筛（如 PE<20, ROE>15%）
3. AI 评估：将特征输入 AI → 获取评分和建议
4. 推荐理由生成：AI 生成自然语言解释
```

---

### 11. 多模态分析模块

**功能描述**：集成更多维度的分析数据。

**实现内容**：
- 龙虎榜数据分析（游资动向）
- 资金流向分析（北向资金、主力资金、散户资金）
- 行业板块轮动分析
- 概念题材热度分析

**技术栈**：
- 数据爬虫
- 数据聚合分析
- 时间序列分析

---

### 12. API 网关模块（可选）

**功能描述**：统一入口，处理跨模块通用逻辑。

**实现内容**：
- 统一鉴权
- 限流熔断（Resilience4j 已有部分）
- 请求日志
- 路由转发
- 灰度发布支持

**技术栈**：
- Spring Cloud Gateway
- Redis（限流）
- Sentinel（熔断降级）

---

## 模块依赖关系图

```
                    ┌─────────────────┐
                    │   stock-user    │ (基础)
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │ stock-alert │   │  Redis 层   │   │  WebSocket  │
    └──────┬──────┘   └──────┬──────┘   └──────┬──────┘
           │                 │                 │
           └─────────────────┼─────────────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │  stock-kline│   │stock-finance│   │stock-news   │
    └──────┬──────┘   └──────┬──────┘   └──────┬──────┘
           │                 │                 │
           └─────────────────┼─────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ stock-backtest  │
                    │  AI 策略推荐    │
                    └─────────────────┘
```

---

## 快速开始建议

**第一阶段（1-2 周）**：
1. 创建 `stock-user` 模块，实现 JWT 登录
2. 引入 Redis，实现基础缓存

**第二阶段（2-3 周）**：
3. 实现 `stock-alert` 预警模块
4. 实现 `WebSocket` 推送

**第三阶段（2-3 周）**：
5. 实现 `stock-kline` K 线服务
6. 实现 `stock-finance` 财务数据

**第四阶段（按需）**：
7. 回测引擎、舆情分析、AI 推荐等高级功能

---

*最后更新：2026-03-03*
