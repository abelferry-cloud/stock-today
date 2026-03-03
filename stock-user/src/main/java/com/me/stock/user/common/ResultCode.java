package com.me.stock.user.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Jovan
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
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1002, "用户已被禁用"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1003, "用户不存在"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1004, "用户名已存在"),

    /**
     * 手机号已存在
     */
    PHONE_EXISTS(1005, "手机号已存在"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS(1006, "邮箱已存在"),

    /**
     * 密码不一致
     */
    PASSWORD_NOT_MATCH(1007, "两次输入的密码不一致"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1008, "Token 已过期"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1009, "Token 无效"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 内部服务器错误
     */
    INTERNAL_ERROR(500, "内部服务器错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
