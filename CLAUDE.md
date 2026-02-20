# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Multi-module Maven project for AI-driven stock analysis platform. The system consists of three main modules that work together:

- **stock-common**: Shared library containing entities, mappers, DTOs, and utilities used by other modules
- **stock-crawler**: Scheduled data collection service that crawls stock market data and publishes to message queues
- **stock-analysis-ai**: Spring AI-based service providing RAG-powered chat for stock analysis

## Project Process

1. **Data Collection**: `stock-crawler` uses XXL-Job to schedule tasks that collect stock data (domestic and international) and publish it to RabbitMQ queues.
2. **Data Processing**: `stock-analysis-ai` consumes messages from RabbitMQ queues and processes the data using a RAG (Retrieval-Augmented Generation) pipeline.
3. **AI Analysis**: `stock-analysis-ai` uses the RAG pipeline to vectorize the data, store it in Pinecone, and provide chat functionality for stock analysis.
4. **Chat Functionality**: `stock-analysis-ai` provides a chat interface for users to ask questions and receive answers based on the processed data.

## Build Commands

```bash
# Build entire project
mvn clean install

# Build specific module
mvn clean package -pl stock-crawler -am

# Run a single test
mvn test -Dtest=ApiKeyConfigTest -pl stock-crawler
```

## Running the Applications

### stock-crawler (Data Collection Service)
Requires XXL-Job executor and runs with profiles:
```bash
java -jar stock-crawler/target/stock-crawler.jar --spring.profiles.active=mq,xxljob,stock
```

### stock-analysis-ai (AI Analysis Service)
Main Spring Boot application:
```bash
java -jar stock-analysis-ai/target/stock-analysis-ai.jar
```
Runs on port 8080. API docs available at `/swagger-ui.html`.

## Architecture

### Data Flow
1. **stock-crawler** collects stock data via XXL-Job scheduled tasks:
   - `getInnerMarketInfo`: Domestic market indices (SSE, SZSE)
   - `getStockRTIndex`: Real-time individual stock data
   - `getOuterMarketInfo`: International indices (Dow Jones, Nasdaq, Hang Seng, etc.)
2. Data is published to **RabbitMQ** queues
3. **stock-analysis-ai** consumes messages via `StockDataQueueListener` and `StockDataConsumerImpl`
4. RAG pipeline vectorizes and stores in Pinecone for knowledge retrieval

### RAG Implementation
- Knowledge base files: `stock-analysis-ai/src/main/resources/static/knowledge/`
- System prompt: `stock-analysis-ai/src/main/resources/static/system-prompt.md`
- Embedding: Alibaba DashScope (text-embedding-v3, 1024 dimensions)
- Chat: DeepSeek (deepseek-chat model)
- Vector Store: Pinecone

### Key Technologies
- Spring Boot 3.2.5, Java 17
- Spring AI 1.0.0 for LLM integration
- XXL-Job 2.4.0 for distributed scheduling
- RabbitMQ for message queuing
- MyBatis for database access
- Knife4j for API documentation

## API Endpoints

### stock-analysis-ai
- `POST /chat` - Synchronous chat with AI
- `POST /chat/stream` - Streaming chat with AI
- `GET /api/mq/status` - RabbitMQ connection status
- `GET /api/mq/queues/info` - Queue information

### XXL-Job (stock-crawler)
- `getInnerMarketInfo` - Collect domestic market indices
- `getStockRTIndex` - Collect real-time stock data
- `getOuterMarketInfo` - Collect international market data

## Configuration

Configuration is profile-based with YAML files in module resources:
- `application.yml` - Default config
- `application-stock.yml` - Stock-specific settings
- `application-mq.yml` - RabbitMQ settings
- `application-xxljob.yml` - XXL-Job settings