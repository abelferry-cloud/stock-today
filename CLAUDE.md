# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Stock Today** is an AI-powered stock analysis platform built as a multi-module Maven project with Spring Boot 3.2.5 and Java 17. The platform combines real-time stock data crawling with AI-powered intelligent analysis using RAG (Retrieval-Augmented Generation).

## Build Commands

```bash
# Build entire project (from root)
mvn clean install

# Build specific module
mvn clean install -pl stock-analysis-ai
mvn clean install -pl stock-crawler
mvn clean install -pl stock-common

# Build without running tests
mvn clean install -DskipTests

# Run tests
mvn test

# Run the AI application (port 8080, context-path: /api)
cd stock-analysis-ai && mvn spring-boot:run

# Run the crawler application
cd stock-crawler && mvn spring-boot:run
```

## Module Architecture

```
stock-today/
├── stock-common/          # Shared module - entities, mappers, DTOs
├── stock-analysis-ai/     # Main AI service (port 8080, /api)
└── stock-crawler/         # Scheduled data crawler with XXL-Job
```

### stock-common
Shared library containing:
- **MyBatis mappers** for database operations (StockRtInfo, StockBusiness, etc.)
- **Entity classes** mapped to MySQL tables
- **Domain objects** (Stock4EvrDayDomain, StockUpdownDomain, etc.)
- **System entities** (SysUser, SysRole, SysPermission - for user management)
- **RabbitMQ configuration properties**
- **Utility classes** (DateTimeUtil, IdWorker, ParserStockInfoUtil)

### stock-analysis-ai (Main Application)
Primary service running on port 8080 with context-path `/api`.

**Entry Point**: `com.stock.platform.StockAnalysisApplication`

**Key Features**:
- **AI Chat Interface**: Regular (`POST /api/chat/send`) and streaming (`POST /api/chat/stream`) endpoints
- **RAG System**: Retrieves stock data from Pinecone vector store to enhance AI responses
- **Conversation Management**: Create/retrieve/delete conversations with message history
- **API Key Rotation**: Manages multiple DeepSeek API keys with usage monitoring
- **Vector Store Consumer**: Listens for stock data updates via RabbitMQ

**Key Services**:
- `ChatService` - Handles AI chat with RAG context injection
- `RagRetrievalService` - Retrieves relevant documents from Pinecone based on query
- `ConversationService` - Manages conversation persistence
- `ApiKeyRotationService` - Rotates API keys for load balancing and monitoring

**Configuration** (`application.yml`):
- DeepSeek as primary AI model (`deepseek-reasoner`)
- Aliyun DashScope (OpenAI-compatible) as secondary model for embeddings
- Pinecone vector store (index: `stock-analysis`)
- MySQL, Redis, RabbitMQ integration

### stock-crawler
Data collection service running scheduled tasks via XXL-Job.

**Entry Point**: `com.me.spring.jobApplication`

**Key Features**:
- **XXL-Job Integration**: Distributed task scheduling
- **RabbitMQ Producer**: Publishes crawled stock data for AI module consumption
- **HttpClient**: Web scraping for stock data

**Key Classes**:
- `StockJob` - XXL-Job task handlers
- `StockTimerTaskService` - Scheduled crawling logic

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2.5, Java 17 |
| Build | Maven (multi-module) |
| Database | MySQL with MyBatis |
| Cache | Redis (Lettuce) |
| Message Queue | RabbitMQ |
| AI | Spring AI 1.0.0, DeepSeek, OpenAI/Aliyun |
| Vector Store | Pinecone |
| Scheduling | XXL-Job 2.4.0 |
| API Docs | Knife4j 4.5.0 (OpenAPI 3) |
| Streaming | Spring WebFlux |

## RAG Architecture

The platform uses Retrieval-Augmented Generation for intelligent stock analysis:

1. **User Query** -> `ChatController`
2. **Keyword Detection** -> `RagRetrievalService.requiresRagRetrieval()` checks for stock-related terms
3. **Vector Search** -> `RagRetrievalService.retrieveRelevantContext()` queries Pinecone
4. **Context Injection** -> Retrieved stock data is injected into AI prompt
5. **AI Response** -> DeepSeek model generates analysis with context

**RAG Parameters** (configurable in `application.yml`):
- `app.rag.chunk-size`: 1000 (document chunking)
- `app.rag.chunk-overlap`: 200
- `app.rag.top-k`: 5 (results returned)
- `app.rag.similarity-threshold`: 0.7

## Environment Variables

The application requires these environment variables (or set in `application.yml`):
- `DEEPSEEK_API_KEY` - Primary DeepSeek API key
- `DEEPSEEK_API_KEY01` through `DEEPSEEK_API_KEY04` - Additional keys for rotation
- `DASHSCOPE_API_KEY` - Aliyun DashScope API key (embeddings)
- `PINECONE_API_KEY` - Pinecone vector store API key

## Database Schema

The platform uses MySQL with the following key entity types:
- **Stock Data**: `StockRtInfo`, `StockBlockRtInfo`, `StockMarketIndexInfo`, `StockOuterMarketIndexInfo`
- **Business Data**: `StockBusiness`
- **User Management**: `SysUser`, `SysRole`, `SysPermission`, `SysUserRole`, `SysRolePermission`
- **System Logs**: `SysLog`
- **AI Module**: `Conversation`, `Message`

MyBatis mapper XML files are located in `src/main/resources/mapper/`.

## API Documentation

Access Knife4j API documentation at: `http://localhost:8080/api/doc.html`

## Important Notes

- The codebase uses **Chinese comments** with English class/method names
- MyBatis requires `-parameters` compiler flag (configured in parent POM)
- Mapper scanning: `com.stock.platform.mapper` (AI module), `com.me.stock.mapper` (crawler/common)
- RabbitMQ virtual host: `/stock-today`
- No test files exist yet - testing framework needs implementation
