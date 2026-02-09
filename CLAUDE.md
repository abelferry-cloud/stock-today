# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI and Distributed Architecture-Based Intelligent Stock Data Analysis Platform. A comprehensive stock data analysis system covering data collection (crawling), cleaning, storage, RAG knowledge base construction, and AI-driven natural language querying.

**Tech Stack:** Java 17, Spring Boot 3.2.5, Spring AI 1.0.0, MyBatis, MySQL, Redis, RabbitMQ, DeepSeek AI

## Build and Run Commands

```bash
# Build and compile
mvn clean compile

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run application (development)
mvn spring-boot:run

# Package as JAR
mvn clean package

# Run JAR directly
java -jar target/stock-analysis-platform-1.0.0.jar
```

## Architecture Overview

### Layer Architecture

The application follows a strict layered architecture with clear separation of concerns:

```
Controller Layer (controller/)    → Thin HTTP request/response handling
              ↓
Service Layer (service/)          → Business logic, validation, orchestration
              ↓
Integration Layer                 → External service calls (AI, DB, cache)
```

**Key Principle:** Controllers should be thin - only handle HTTP I/O. All business logic belongs in the service layer.

### Service Layer Organization

- **`ChatService`** - Business logic for chat operations (validation, error handling, logging)
- **`DeepSeekService`** - AI integration layer (direct Spring AI ChatClient wrapper)

When adding new features, follow this pattern:
1. Create a business logic service (`*Service`) that handles validation and orchestration
2. Create an integration service for external API calls if needed
3. Keep controllers thin - just delegate to services and wrap responses

### Response Wrapper Pattern

The project uses **`Result<T>`** as the standardized API response wrapper (not the old `Response<T>`). Always return `Result<T>` from controllers:

```java
return Result.success(data);
return Result.error(ResultCode.SOME_ERROR, "message");
```

Exception handling is centralized in `GlobalExceptionHandler` - it automatically converts exceptions to `Result<?>` responses.

### AI Integration (Spring AI)

The project uses Spring AI with DeepSeek API (OpenAI-compatible). Key configuration in `application.yml`:

```yaml
spring.ai.openai:
  api-key: ${DEEPSEEK_API_KEY:...}
  base-url: https://api.deepseek.com
  chat.options.model: deepseek-chat
```

**Streaming Responses:** The application supports streaming chat responses via WebFlux `Flux<String>`. Use `chatClient.prompt().stream().content()` for streaming, `chatClient.prompt().call().content()` for regular responses.

### Async Processing

The application has three async mechanisms:
1. **`@EnableAsync`** - For simple async method execution
2. **RabbitMQ** - Message queue for durable async processing (configured in `RabbitMQConfig`)
3. **`@EnableScheduling`** - For scheduled tasks (e.g., periodic stock data crawling)

When implementing the stock crawling pipeline, use RabbitMQ queues defined in `RabbitMQConfig`:
- `stock.crawl.queue` - Trigger crawling tasks
- `stock.data.queue` - Process crawled data
- `rag.build.queue` - Async RAG knowledge base updates

### Database Layer (MyBatis)

- MyBatis XML mappers go in `src/main/resources/mapper/`
- Mapper interfaces go in `mapper/`
- Entity classes go in `entity/`
- Configuration: `map-underscore-to-camel-case: true` (database column `stock_name` → entity field `stockName`)

### RAG Knowledge Base

Redis is configured as both cache and vector store for RAG:
```yaml
spring.ai.vectorstore.redis:
  uri: redis://localhost:6379
  index-name: stock-knowledge
  prefix: "doc:"
```

RAG configuration in `application.yml`:
- `app.rag.chunk-size: 1000`
- `app.rag.chunk-overlap: 200`
- `app.rag.top-k: 5`

## Project Implementation Status

**Implemented:**
- Chat functionality with streaming support (`ChatController`, `ChatService`)
- Infrastructure configurations (MySQL, Redis, RabbitMQ, MyBatis, Spring AI)
- Unified response wrappers (`Result<T>`)
- Global exception handling

**Planned (not yet implemented):**
- Stock data crawling from third-party websites (`crawler/` package)
- Database entities and MyBatis mappers for stock data
- RAG knowledge base construction (`ai/` package)
- RabbitMQ message consumers for async processing
- Scheduled tasks for periodic data crawling

## Configuration Notes

- All external service credentials use environment variable fallbacks (e.g., `${DEEPSEEK_API_KEY:default}`)
- Server runs on port 8080 with context path `/api`
- Logs written to `logs/stock-platform.log` with 100MB rotation
- MyBatis mappers auto-discovered from `classpath:mapper/**/*.xml`

## Adding New Features

1. **New REST endpoint:** Create controller method → add business logic in service → return `Result<T>`
2. **New async task:** Create RabbitMQ listener in `rabbitmq/` with `@RabbitListener`
3. **New database entity:** Create entity class, MyBatis mapper interface, and XML mapper
4. **New scheduled task:** Create method with `@Scheduled` in a service class


## language
Notes: all your output should be in Chinese.

