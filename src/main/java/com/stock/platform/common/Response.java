package com.stock.platform.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Unified Response Wrapper
 * Standardizes API response format across the application
 *
 * @param <T> Response data type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {

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
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data, System.currentTimeMillis());
    }

    /**
     * Success response without data
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "Success", null, System.currentTimeMillis());
    }

    /**
     * Success response with custom message
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * Error response with code and message
     */
    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * Error response with default error code
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null, System.currentTimeMillis());
    }

    /**
     * Error response from exception
     */
    public static <T> Response<T> error(ResponseCode responseCode) {
        return new Response<>(responseCode.getCode(), responseCode.getMessage(), null, System.currentTimeMillis());
    }
}
