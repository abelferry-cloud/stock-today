package com.stock.platform.utils;

import com.google.common.base.Preconditions;
import com.stock.platform.common.BusinessException;
import com.stock.platform.common.ResultCode;

/**
 * Preconditions Utility
 * Wrapper around Guava Preconditions with custom exception handling
 */
public class PreconditionsUtil {

    /**
     * Checks that the given object is not null
     *
     * @param reference the object reference to check
     * @param errorMessage the exception message to use if the check fails
     * @throws BusinessException if {@code reference} is null
     */
    public static void checkNotNull(Object reference, String errorMessage) {
        try {
            Preconditions.checkNotNull(reference, errorMessage);
        } catch (NullPointerException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), errorMessage);
        }
    }

    /**
     * Checks that the given object is not null
     *
     * @param reference the object reference to check
     * @param resultCode the result code to use if the check fails
     * @throws BusinessException if {@code reference} is null
     */
    public static void checkNotNull(Object reference, ResultCode resultCode) {
        try {
            Preconditions.checkNotNull(reference);
        } catch (NullPointerException e) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * Checks that the given expression is true
     *
     * @param expression the expression to check
     * @param errorMessage the exception message to use if the check fails
     * @throws BusinessException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, String errorMessage) {
        try {
            Preconditions.checkArgument(expression, errorMessage);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), errorMessage);
        }
    }

    /**
     * Checks that the given expression is true
     *
     * @param expression the expression to check
     * @param resultCode the result code to use if the check fails
     * @throws BusinessException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, ResultCode resultCode) {
        try {
            Preconditions.checkArgument(expression);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * Checks that the given state is valid
     *
     * @param expression the expression to check
     * @param errorMessage the exception message to use if the check fails
     * @throws BusinessException if {@code expression} is false
     */
    public static void checkState(boolean expression, String errorMessage) {
        try {
            Preconditions.checkState(expression, errorMessage);
        } catch (IllegalStateException e) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR.getCode(), errorMessage);
        }
    }
}
