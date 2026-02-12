package com.me.spring.stockanalysisai.util;

/**
 * 字符串工具类
 * 
 * @author system
 * @since 1.0.0
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str 字符串
     * @return true: 空, false: 非空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     * 
     * @param str 字符串
     * @return true: 非空, false: 空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     * 
     * @param str 字符串
     * @return true: 空白, false: 非空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空白
     * 
     * @param str 字符串
     * @return true: 非空白, false: 空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
