package com.me.stock.user.common;

import lombok.Getter;

/**
 * 返回状态码枚举
 *
 * @author stock-user
 */
@Getter
public enum ResultCode {

    // 通用状态码 (200-599)
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 客户端错误 (400-499)
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // Token 相关 (1000-1999)
    TOKEN_EMPTY(1001, "Token 为空"),
    TOKEN_INVALID(1002, "Token 无效"),
    TOKEN_EXPIRED(1003, "Token 已过期"),
    TOKEN_REFRESH_FAILED(1004, "Token 刷新失败"),

    // 用户相关 (2000-2999)
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    USER_PASSWORD_ERROR(2003, "用户名或密码错误"),
    USER_DISABLED(2004, "用户已被禁用"),
    USER_PHONE_EXISTS(2005, "手机号已被注册"),
    USER_EMAIL_EXISTS(2006, "邮箱已被注册"),

    // 角色相关 (3000-3999)
    ROLE_NOT_FOUND(3001, "角色不存在"),
    ROLE_ALREADY_EXISTS(3002, "角色已存在"),

    // 权限相关 (4000-4999)
    PERMISSION_NOT_FOUND(4001, "权限不存在"),
    PERMISSION_ALREADY_EXISTS(4002, "权限已存在"),

    // 系统相关 (5000-5999)
    SYSTEM_ERROR(5001, "系统内部错误"),
    DATABASE_ERROR(5002, "数据库操作失败"),
    REDIS_ERROR(5003, "Redis 操作失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
