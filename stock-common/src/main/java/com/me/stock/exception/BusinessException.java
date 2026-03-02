package com.me.stock.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException{
    /**
     * 错误码 项目中一般使用约定好的枚举类
     */
    @Getter
    private int code;
    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code=code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
