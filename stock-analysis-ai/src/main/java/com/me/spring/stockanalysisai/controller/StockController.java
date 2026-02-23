package com.me.spring.stockanalysisai.controller;

import com.me.spring.stockanalysisai.common.Result;
import com.me.spring.stockanalysisai.service.StockDataService;
import com.me.stock.pojo.domain.Stock4EvrDayDomain;
import com.me.stock.pojo.domain.Stock4MinuteDomain;
import com.me.stock.pojo.domain.StockUpdownDomain;
import com.me.stock.pojo.domain.StockWeeklyInfoDomain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 股票数据查询控制器
 *
 * @author system
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Validated
@Tag(name = "StockController", description = "股票数据查询接口")
public class StockController {

    private final StockDataService stockDataService;

    /**
     * 获取个股日K线数据
     *
     * @param code      股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日K线数据列表
     */
    @GetMapping("/dayKLine")
    @Operation(summary = "获取日K线数据", description = "获取个股日K线数据")
    public Result<List<Stock4EvrDayDomain>> getDayKLine(
            @Parameter(description = "股票代码", example = "000001") @RequestParam String code,
            @Parameter(description = "开始日期", example = "2024-01-01") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2024-12-31") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Stock4EvrDayDomain> data = stockDataService.getDayKLine(code, startDate, endDate);
        return Result.success(data);
    }

    /**
     * 获取个股周K线数据
     *
     * @param code 股票代码
     * @param date 查询日期
     * @return 周K线数据
     */
    @GetMapping("/weekKLine")
    @Operation(summary = "获取周K线数据", description = "获取个股周K线数据")
    public Result<StockWeeklyInfoDomain> getWeekKLine(
            @Parameter(description = "股票代码", example = "000001") @RequestParam String code,
            @Parameter(description = "查询日期", example = "2024-01-15") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        StockWeeklyInfoDomain data = stockDataService.getWeekKLine(code, date);
        return Result.success(data);
    }

    /**
     * 获取个股分时数据
     *
     * @param code      股票代码
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 分时数据列表
     */
    @GetMapping("/minuteKLine")
    @Operation(summary = "获取分时数据", description = "获取个股分时数据（分钟K线）")
    public Result<List<Stock4MinuteDomain>> getMinuteKLine(
            @Parameter(description = "股票代码", example = "000001") @RequestParam String code,
            @Parameter(description = "开始时间", example = "2024-01-15 09:30:00") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2024-01-15 15:00:00") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Stock4MinuteDomain> data = stockDataService.getMinuteKLine(code, startTime, endTime);
        return Result.success(data);
    }

    /**
     * 获取涨幅排行
     *
     * @param timePoint 时间点
     * @return 涨幅排行列表
     */
    @GetMapping("/ranking")
    @Operation(summary = "获取涨幅排行", description = "获取股票涨幅排行")
    public Result<List<StockUpdownDomain>> getIncreaseRanking(
            @Parameter(description = "时间点", example = "2024-01-15 15:00:00") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timePoint) {
        List<StockUpdownDomain> data = stockDataService.getIncreaseRanking(timePoint);
        return Result.success(data);
    }

    /**
     * 股票搜索
     *
     * @param keyword 搜索关键字（股票代码或名称）
     * @return 搜索结果列表
     */
    @GetMapping("/search")
    @Operation(summary = "股票搜索", description = "根据关键字搜索股票")
    public Result<List<Map>> searchStock(
            @Parameter(description = "搜索关键字", example = "平安") @RequestParam String keyword) {
        List<Map> data = stockDataService.searchStock(keyword);
        return Result.success(data);
    }
}