package com.me.spring.stockanalysisai.util;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

/**
 * 日期时间工具类
 * 
 * @author system
 * @since 1.0.0
 */
public class DateTimeUtil {

    /**
     * 获取当前地区的时间
     * 
     * @return 当前日期时间字符串
     */
    @Tool(description = "获取当前地区的时间")
    public static String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    /**
     * 获取当前时间戳
     * 
     * @return 时间戳（毫秒）
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
