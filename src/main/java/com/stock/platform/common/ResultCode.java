package com.stock.platform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result Code Enum
 * Defines standard response codes and messages for the application
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // Success
    SUCCESS(200, "Success"),

    // Client Errors (4xx)
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    // Server Errors (5xx)
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),

    // Business Error Codes (1000-1999)
    STOCK_NOT_FOUND(1001, "Stock Information Not Found"),
    STOCK_DATA_INVALID(1002, "Invalid Stock Data"),
    STOCK_CRAWL_FAILED(1003, "Failed to Crawl Stock Data"),
    STOCK_ALREADY_EXISTS(1004, "Stock Already Exists"),

    // Chat/AI Error Codes (2000-2999)
    CHAT_SERVICE_ERROR(2001, "Chat Service Error"),
    CHAT_TIMEOUT(2002, "Chat Service Timeout"),
    CHAT_RATE_LIMIT_EXCEEDED(2003, "Chat Rate Limit Exceeded"),
    CHAT_MESSAGE_EMPTY(2004, "Chat Message Cannot Be Empty"),

    // RAG Error Codes (3000-3999)
    RAG_BUILD_FAILED(3001, "Failed to Build RAG Knowledge Base"),
    RAG_QUERY_FAILED(3002, "Failed to Query RAG Knowledge Base"),
    RAG_VECTOR_STORE_ERROR(3003, "Vector Store Error"),

    // Database Error Codes (4000-4999)
    DATABASE_ERROR(4001, "Database Operation Failed"),
    DATA_NOT_FOUND(4002, "Data Not Found in Database"),
    DUPLICATE_DATA(4003, "Duplicate Data"),

    // Cache Error Codes (5000-5999)
    CACHE_ERROR(5001, "Cache Operation Failed"),
    CACHE_MISS(5002, "Cache Miss"),

    // Message Queue Error Codes (6000-6999)
    MQ_ERROR(6001, "Message Queue Operation Failed"),
    MQ_CONSUME_FAILED(6002, "Failed to Consume Message");

    private final Integer code;
    private final String message;
}
