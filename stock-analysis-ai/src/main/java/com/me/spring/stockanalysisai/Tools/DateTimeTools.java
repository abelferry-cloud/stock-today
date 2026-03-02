package com.me.spring.stockanalysisai.Tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日期时间工具类
 * 提供当前日期时间查询功能
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Component
public class DateTimeTools {

    @Tool(description = "获取当前日期时间，返回格式为 ISO-8601 字符串。用于处理股票查询时确定'今天'、'昨天'等相对日期")
    public String getCurrentDateTime() {
        String result = LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        log.info("获取当前日期时间：result={}", result);
        return result;
    }

}