package com.stock.platform.common.constant;

import lombok.Getter;

/**
 * 统一返回码枚举
 */
@Getter
public enum ResultCode {

    // 通用
    SUCCESS(200, "操作成功"),
    ERROR(500, "服务器内部错误"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误 5xx
    BUSINESS_ERROR(5000, "业务处理异常"),
    AI_SERVICE_ERROR(5001, "AI服务调用失败"),
    INVALID_MESSAGE(5002, "消息内容为空"),
    INVALID_API_KEY(5003, "API Key无效或已耗尽");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
