package com.me.spring.stockanalysisai.Tools;

import com.me.stock.mapper.StockMarketIndexInfoMapper;
import com.me.stock.mapper.StockOuterMarketIndexInfoMapper;
import com.me.stock.mapper.StockRtInfoMapper;
import com.me.stock.pojo.domain.ExternalIndexDomain;
import com.me.stock.pojo.domain.InnerMarketDomain;
import com.me.stock.pojo.domain.Stock4EvrDayDomain;
import com.me.stock.pojo.domain.Stock4MinuteDomain;
import com.me.stock.pojo.domain.StockRTMainBusinessDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票数据库查询工具类
 * 提供股票实时数据、K 线数据、大盘指数等数据库查询能力
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockQueryTool {

    private final StockRtInfoMapper stockRtInfoMapper;
    private final StockMarketIndexInfoMapper marketIndexMapper;
    private final StockOuterMarketIndexInfoMapper outerMarketMapper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 查询股票实时行情数据
     *
     * @param stockCode 股票代码（如：600519）
     * @param timePoint 查询时间点（如：2025-03-01）
     * @return 股票实时行情数据
     */
    @Tool(description = "查询指定股票的实时行情数据，包括当前价格、涨跌幅、成交量等。参数：stockCode-股票代码（如 600519），timePoint-查询时间点（yyyy-MM-dd 格式）")
    public Stock4MinuteDomain queryStockRealTimeData(String stockCode, String timePoint) {
        try {
            Date time = DATE_FORMAT.parse(timePoint);
            Stock4MinuteDomain result = stockRtInfoMapper.getScreenDetail(stockCode, time);
            log.info("查询股票实时数据：code={}, time={}, result={}", stockCode, timePoint, result != null ? "success" : "null");
            return result;
        } catch (Exception e) {
            log.error("查询股票实时数据失败：code={}, timePoint={}, error={}", stockCode, timePoint, e.getMessage());
            return null;
        }
    }

    /**
     * 查询股票日 K 线数据
     *
     * @param stockCode 股票代码
     * @param startDate 开始日期（yyyy-MM-dd 格式）
     * @param endDate 结束日期（yyyy-MM-dd 格式）
     * @return 日 K 线数据列表
     */
    @Tool(description = "查询股票日 K 线数据，包括每日的开盘价、收盘价、最高价、最低价、成交量等。参数：stockCode-股票代码，startDate-开始日期（yyyy-MM-dd 格式），endDate-结束日期（yyyy-MM-dd 格式）")
    public List<Stock4EvrDayDomain> queryStockKLineData(String stockCode, String startDate, String endDate) {
        try {
            Date start = DATE_FORMAT.parse(startDate);
            Date end = DATE_FORMAT.parse(endDate);
            List<Stock4EvrDayDomain> result = stockRtInfoMapper.getDayKLineData01(stockCode, start, end);
            log.info("查询股票 K 线数据：code={}, start={}, end={}, resultCount={}", stockCode, startDate, endDate, result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("查询股票 K 线数据失败：code={}, startDate={}, endDate={}, error={}", stockCode, startDate, endDate, e.getMessage());
            return null;
        }
    }

    /**
     * 查询国内大盘指数
     *
     * @param curDate 查询日期（yyyy-MM-dd 格式）
     * @param marketCodes 大盘编码列表（如：["000001","399001"]，000001=上证指数，399001=深证成指）
     * @return 大盘指数数据列表
     */
    @Tool(description = "查询国内大盘指数数据（上证指数、深证成指、创业板指等）。参数：curDate-查询日期（yyyy-MM-dd 格式），marketCodes-大盘编码列表（如 [\"000001\",\"399001\"]，000001=上证指数，399001=深证成指，399006=创业板指）")
    public List<InnerMarketDomain> queryInnerMarketIndex(String curDate, List<String> marketCodes) {
        try {
            Date date = DATE_FORMAT.parse(curDate);
            List<InnerMarketDomain> result = marketIndexMapper.getMarketInfo(date, marketCodes);
            log.info("查询国内大盘指数：date={}, codes={}, resultCount={}", curDate, marketCodes, result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("查询国内大盘指数失败：date={}, codes={}, error={}", curDate, marketCodes, e.getMessage());
            return null;
        }
    }

    /**
     * 查询国际大盘指数
     *
     * @return 国际大盘指数数据列表（道琼斯、纳斯达克、恒生指数等）
     */
    @Tool(description = "查询国际大盘指数数据（道琼斯、纳斯达克、恒生指数、日经 225 等），无需参数，返回最新的国际指数数据")
    public List<ExternalIndexDomain> queryOuterMarketIndex() {
        try {
            List<ExternalIndexDomain> result = outerMarketMapper.getExternalIndex();
            log.info("查询国际大盘指数：resultCount={}", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("查询国际大盘指数失败：error={}", e.getMessage());
            return null;
        }
    }

    /**
     * 搜索股票（支持代码/名称模糊匹配）
     *
     * @param keyword 搜索关键词（股票代码或股票名称）
     * @return 匹配的股票列表
     */
    @Tool(description = "搜索股票（支持代码或名称模糊匹配）。参数：keyword-搜索关键词（如\"茅台\"或\"600519\"），返回匹配的股票列表")
    public List<Map> searchStock(String keyword) {
        try {
            List<Map> result = stockRtInfoMapper.searchStock(keyword);
            log.info("搜索股票：keyword={}, resultCount={}", keyword, result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("搜索股票失败：keyword={}, error={}", keyword, e.getMessage());
            return null;
        }
    }

    /**
     * 获取个股主要业务数据
     *
     * @param stockCode 股票代码
     * @return 个股主要业务数据
     */
    @Tool(description = "获取个股主要业务数据（主营业务、行业板块等）。参数：stockCode-股票代码")
    public StockRTMainBusinessDomain queryStockMainBusiness(String stockCode) {
        try {
            StockRTMainBusinessDomain result = stockRtInfoMapper.getStockRTMainBusinessDomain(stockCode);
            log.info("查询个股主要业务数据：code={}, result={}", stockCode, result != null ? "success" : "null");
            return result;
        } catch (Exception e) {
            log.error("查询个股主要业务数据失败：code={}, error={}", stockCode, e.getMessage());
            return null;
        }
    }
}
