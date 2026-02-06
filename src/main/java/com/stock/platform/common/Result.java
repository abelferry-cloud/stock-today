package com.stock.platform.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Unified Result Wrapper
 * Standardizes API response format across the application
 *
 * @param <T> Response data type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Response code (200 for success, others for errors)
     */
    private Integer code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Timestamp
     */
    private Long timestamp;

    /**
     * Success response with data
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "Success", data, System.currentTimeMillis());
    }

    /**
     * Success response without data
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "Success", null, System.currentTimeMillis());
    }

    /**
     * Success response with custom message
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * Error response with code and message
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * Error response with default error code
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis());
    }

    /**
     * Error response from exception
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, System.currentTimeMillis());
    }

    /**
     * Error response from code and custom message
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null, System.currentTimeMillis());
    }

    /**
     * Check if result is successful
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
