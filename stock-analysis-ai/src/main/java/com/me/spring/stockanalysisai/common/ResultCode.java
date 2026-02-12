package com.me.spring.stockanalysisai.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求超时
     */
    TIMEOUT(408, "请求超时"),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(429, "系统繁忙，请稍后再试"),

    /**
     * AI服务异常
     */
    AI_SERVICE_ERROR(1001, "AI服务异常"),

    /**
     * 知识库加载失败
     */
    KNOWLEDGE_LOAD_ERROR(1002, "知识库加载失败"),

    /**
     * 聊天上下文不存在
     */
    CONVERSATION_NOT_FOUND(1003, "聊天上下文不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
