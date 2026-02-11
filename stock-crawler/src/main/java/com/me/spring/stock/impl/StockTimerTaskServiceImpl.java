package com.me.spring.stock.impl;

import com.google.common.collect.Lists;
import com.me.spring.stock.StockTimerTaskService;
import com.me.stock.config.RabbitMQProperties;
import com.me.stock.mapper.*;
import com.me.stock.pojo.dto.StockDataMessage;
import com.me.stock.pojo.entity.StockBlockRtInfo;
import com.me.stock.pojo.entity.StockMarketIndexInfo;
import com.me.stock.pojo.entity.StockOuterMarketIndexInfo;
import com.me.stock.pojo.entity.StockRtInfo;
import com.me.stock.pojo.vo.StockInfoConfig;
import com.me.stock.utils.DateTimeUtil;
import com.me.stock.utils.IdWorker;
import com.me.stock.utils.ParseType;
import com.me.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.io.NumberInput.parseBigDecimal;

@Slf4j
@Service
public class StockTimerTaskServiceImpl implements StockTimerTaskService {

    // ==================== 数据类型常量 ====================
    private static final String DATA_TYPE_INNER_MARKET = "INNER_MARKET";
    private static final String DATA_TYPE_OUTER_MARKET = "OUTER_MARKET";
    private static final String DATA_TYPE_STOCK_RT = "STOCK_RT";
    private static final String DATA_TYPE_BLOCK_SECTOR = "BLOCK_SECTOR";
    private static final String SOURCE_SINA = "新浪财经";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 请求头
     */
    private HttpEntity<Object> httpEntity;
    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;

    /**
     * 获取国内大盘股票信息
     */
    @Override
    public void getInnerMarketInfo() {
        //1. 采集原始数据
        //1.1 拼接URL
        String url=stockInfoConfig.getMarketURL()+String.join(",",stockInfoConfig.getInner());
        // 真实URL地址：http://hq.sinajs.cn/list=sh000001,sz399001

        //1.2 请求头 添加防盗链和用户标识
        initData();

        //2。 resetTemplate发起请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCodeValue();
        if (statusCode!=200) {
            log.error("当前时间: {},采集国内大盘数据失败，状态码: {}：", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), responseEntity.getStatusCode());
            //后续业务，比如：发送邮件，企业微信 钉钉给管理员
            return;
        }
        //3. java正则解析原始数据
        // 3.1 获取原始数据
        String jsData = responseEntity.getBody();
        if (jsData==null){
            log.error("当前时间: {},原始(js格式)国内大盘数据为空", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        log.info("当前时间: {},原始(js格式)国内大盘数据: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), jsData);

        // 3.2 创建正则表达式
        String reg="var hq_str_(.+)=\"(.+)\";";

        //3.3 编译表达式,获取编译对象
        Pattern pattern = Pattern.compile(reg);

        //3.4 匹配字符串
        Matcher matcher = pattern.matcher(jsData);
        ArrayList<StockMarketIndexInfo> entities = new ArrayList<>();
        //4. 封装数据
        while (matcher.find()){
            //1.获取大盘的股票
            String marketCode = matcher.group(1);
            //2. 获取其他信息
            String otherInfo = matcher.group(2);
            //3.讲other字符串以逗号切割，获取数据
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间
            Date curTime = java.sql.Timestamp.valueOf(DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]));
            //组装entity对象
            StockMarketIndexInfo entity = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId())
                    .marketCode(marketCode)
                    .marketName(marketName)
                    .curPoint(curPoint)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeVolume(tradeVol)
                    .tradeAmount(tradeAmt)
                    .curTime(curTime)
                    .build();
            //收集封装的对象，方便批量插入
            entities.add(entity);
        }
        log.info("解析大盘数据完毕...");
        //
        int count =stockMarketIndexInfoMapper.insertBatch(entities);
        if (count>0){
            // 大盘数据采集完毕，通知backend工程刷新缓存
            //发送日期对象，接受方通过接受的日期与当前日期对比，能判断数据延迟的时长，用于运维通知处理
            rabbitTemplate.convertAndSend("stockExchange", "inner.market",new Date());
            log.info("当前时间: {},批量大盘插入成功，数量: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), count);

            // 发送数据到RAG知识库向量队列
            for (StockMarketIndexInfo entity : entities) {
                String content = String.format(
                        "国内大盘指数信息 - 代码: %s, 名称: %s, 当前点数: %s, 开盘点数: %s, " +
                        "前收盘点: %s, 最高点: %s, 最低点: %s, 成交量: %s, 成交金额: %s, 时间: %s",
                        entity.getMarketCode(), entity.getMarketName(), entity.getCurPoint(),
                        entity.getOpenPoint(), entity.getPreClosePoint(), entity.getMaxPoint(),
                        entity.getMinPoint(), entity.getTradeAmount(), entity.getTradeVolume(),
                        entity.getCurTime()
                );
                sendToVectorQueue(entity.getMarketCode(), entity.getMarketName(),
                        DATA_TYPE_INNER_MARKET, "国内大盘指数-" + entity.getMarketName(), content);
            }
        }else {
            log.error("当前时间: {},批量大盘插入失败", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    /**
     * 批量获取股票分时数据详情信息
     * <a href="http://hq.sinajs.cn/list=sz000002,sh600015">...</a>
     */
    @Override
    public void getStockRTIndex() {
        //1. 获取所有个股的集合 3000+
        List<String> allStockCodes = stockBusinessMapper.getALLStockCodes();
        //2. 一次性将所有集合拼接到url地址中
        //2.1 添加业务前缀 以"6"开头的代码添加"sh"前缀（上海证券交易所），其他代码添加"sz"前缀（深圳证券交易所）
        List<String> allCodes = allStockCodes.stream()
                .map(code -> code.startsWith("6") ? "sh" + code : "sz" + code)
                .collect(Collectors.toList());

        //2.2 分组  将大的集合切成若干小集合，分批次插入数据库
        Lists.partition(allCodes, 15).forEach(codes->{
            threadPoolTaskExecutor.execute(() -> {
                //2.2.1 拼接url
                String url = stockInfoConfig.getMarketURL() + String.join(",", codes);

                //2.2.2 请求头 添加防盗链和用户标识
                initData();

                //2.2.3 发起请求
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
                int statusCode = responseEntity.getStatusCodeValue();
                if (statusCode != 200) {
                    log.error("当前时间: {},采集数据失败，状态码: {}：", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), responseEntity.getStatusCode());
                    return;
                }
                String jsData = responseEntity.getBody();
                log.info("当前时间: {},原始数据: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), jsData);

                List list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
                log.info("解析个股数据: {}", list);

                // 使用重试机制插入数据，避免死锁导致任务失败
                int count = insertBatchWithRetry(list, 3);
                if (count > 0) {
                    log.info("当前时间: {},批量插入个股数据成功，数量: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), list.size());

                    // 发送数据到RAG知识库向量队列
                    for (Object obj : list) {
                        if (obj instanceof StockRtInfo stock) {
                            String content = String.format(
                                    "个股实时行情信息 - 代码: %s, 名称: %s, 当前价格: %s, " +
                                    "开盘价: %s, 前收盘价: %s, 最高价: %s, 最低价: %s, " +
                                    "成交量: %s, 成交金额: %s, 时间: %s",
                                    stock.getStockCode(), stock.getStockName(), stock.getCurPrice(),
                                    stock.getOpenPrice(), stock.getPreClosePrice(), stock.getMaxPrice(),
                                    stock.getMinPrice(), stock.getTradeAmount(), stock.getTradeVolume(),
                                    stock.getCurTime()
                            );
                            sendToVectorQueue(stock.getStockCode(), stock.getStockName(),
                                    DATA_TYPE_STOCK_RT, "个股实时行情-" + stock.getStockName(), content);
                        }
                    }
                } else {
                    log.error("当前时间: {},批量插入个股数据失败", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            });
        });
    }

    /**
     * 板块实时数据采集
     */
    @Override
    public void getStockSectorRtIndex() {
        //1. 采集原始数据
        //1.1 获取板块的url
        String url=stockInfoConfig.getBlockURL();

        //1.2 发送板块数据请求
        String result = restTemplate.getForObject(url, String.class);

        //2. 响应结果转板块集合数据
        List<StockBlockRtInfo> infos = parserStockInfoUtil.parse4StockBlock(result);
        log.info("板块数据: {}", infos);

        //3. 数据分片保存到数据库下 行业板块类目大概50个，可每小时查询一次即可
        Lists.partition(infos,20).forEach(list->{
            //20个一组，批量插入
            stockBlockRtInfoMapper.insertBatch(list);
            log.info("批量插入板块数据成功，数量: {}", list.size());

            // 发送数据到RAG知识库向量队列
            for (StockBlockRtInfo block : list) {
                String content = String.format(
                        "板块实时行情信息 - 标识: %s, 板块名称: %s, 公司数量: %s, " +
                        "平均价格: %s, 涨跌幅: %s, 交易量: %s, 交易金额: %s, 时间: %s",
                        block.getLabel(), block.getBlockName(), block.getCompanyNum(),
                        block.getAvgPrice(), block.getUpdownRate(), block.getTradeAmount(),
                        block.getTradeVolume(), block.getCurTime()
                );
                sendToVectorQueue(block.getLabel(), block.getBlockName(),
                        DATA_TYPE_BLOCK_SECTOR, "板块实时行情-" + block.getBlockName(), content);
            }
        });

    }

    /**
     * 外盘实时数据采集
     */
    @Override
    public void getOuterMarketInfo() {
        //1. 获取外盘数据
        //1.1 拼接URL
        String url=stockInfoConfig.getMarketURL()+String.join(",",stockInfoConfig.getOuter());

        //1.2 请求头 添加防盗链和用户标识
        initData();

        //2. resetTemplate发起请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        int statusCode = responseEntity.getStatusCodeValue();
        if (statusCode!=200) {
            log.error("当前时间: {},采集外盘数据失败，状态码: {}：", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), responseEntity.getStatusCode());
            //后续业务，比如：发送邮件，企业微信 钉钉给管理员
            return;
        }

        //3. java正则解析原始数据
        // 3.1 获取原始数据
        String rawData = responseEntity.getBody();
        if (rawData==null){
            log.error("当前时间: {},原始(js格式)外盘数据为空", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return;
        }
        log.info("当前时间: {},原始(js格式)外盘数据: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), rawData);

        // 3.2 按行分割处理数据
        String[] lines = rawData.split(";\\s*");
        ArrayList<StockOuterMarketIndexInfo> entities = new ArrayList<>();

        //4. 逐行解析数据
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            try {
                // 使用正则表达式匹配每行数据
                String regex = "var hq_str_(\\w+)=\"([^\"]+)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    String marketCode = matcher.group(1);
                    String content = matcher.group(2);

                    // 分割内容字段
                    String[] fields = content.split(",");

                    if (fields.length >= 4) {
                        String marketName = fields[0];
                        BigDecimal curPoint = parseBigDecimal(fields[1]);
                        BigDecimal updown = parseBigDecimal(fields[2]);
                        BigDecimal rose = parseBigDecimal(fields[3]); // 只取前4个必需字段

                        // 时间
                        Date curTime = java.sql.Timestamp.valueOf(DateTimeUtil.getDateTimeWithoutSecond(java.time.LocalDateTime.now()));

                        //组装entity对象
                        StockOuterMarketIndexInfo entity = StockOuterMarketIndexInfo.builder()
                                .id(idWorker.nextId())
                                .marketCode(marketCode)
                                .marketName(marketName)
                                .curPoint(curPoint)
                                .updown(updown)
                                .rose(rose)
                                .curTime(curTime)
                                .build();

                        //收集封装的对象，方便批量插入
                        entities.add(entity);
                        log.debug("成功解析外盘数据: {} - {}", marketCode, marketName);

                    } else {
                        log.warn("字段不足，跳过该行: {}", line);
                    }
                } else {
                    log.warn("正则匹配失败，跳过该行: {}", line);
                }
            } catch (Exception e) {
                log.error("解析外盘数据行失败: {}，错误信息: {}", line, e.getMessage());
                // 继续处理其他行，不中断整个流程
            }
        }

        if (entities.isEmpty()) {
            log.error("当前时间: {},未解析到任何有效的外盘数据", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return;
        }

        log.info("解析外盘数据完毕，共解析 {} 条数据...", entities.size());

        // 5. 批量插入
        try {
            int count = stockOuterMarketIndexInfoMapper.insertBatch(entities);
            if (count > 0) {
                // 大盘数据采集完毕，通知backend工程刷新缓存
                //发送日期对象，接受方通过接受的日期与当前日期对比，能判断数据延迟的时长，用于运维通知处理
                rabbitTemplate.convertAndSend("stockExchange", "stock.outer.market", new Date());
                log.info("当前时间: {},批量外盘插入成功，数量: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), count);

                // 发送数据到RAG知识库向量队列
                for (StockOuterMarketIndexInfo entity : entities) {
                    String content = String.format(
                            "国外大盘指数信息 - 代码: %s, 名称: %s, 当前点数: %s, 涨跌: %s, 涨跌幅: %s, 时间: %s",
                            entity.getMarketCode(), entity.getMarketName(), entity.getCurPoint(),
                            entity.getUpdown(), entity.getRose(), entity.getCurTime()
                    );
                    sendToVectorQueue(entity.getMarketCode(), entity.getMarketName(),
                            DATA_TYPE_OUTER_MARKET, "国外大盘指数-" + entity.getMarketName(), content);
                }
            } else {
                log.error("当前时间: {},批量外盘插入失败", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        } catch (Exception e) {
            log.error("当前时间: {},批量插入外盘数据异常: {}", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), e.getMessage());
        }
    }

    /**
     * 安全解析BigDecimal方法，处理可能包含额外字符的数字字符串
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // 移除任何非数字字符（除了负号、小数点和逗号）
            String cleanValue = value.replaceAll("[^\\d.,-]", "").trim();

            // 处理可能存在的逗号（千分位分隔符）
            cleanValue = cleanValue.replace(",", "");

            if (cleanValue.isEmpty()) {
                return BigDecimal.ZERO;
            }

            // 解析为BigDecimal并设置精度
            return new BigDecimal(cleanValue).setScale(2, RoundingMode.HALF_UP);

        } catch (NumberFormatException e) {
            log.error("数字格式转换失败: '{}'", value);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 初始化数据 设置请求头 添加防盗链和用户标识
     */
    @PostConstruct
    public void initData(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer","https://finance.sina.com.cn/stock/"); //防盗链
        httpHeaders.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        //2.2 组装请求对象
        httpEntity = new HttpEntity<>(httpHeaders);
    }


    /**
     * 发送股票数据到RAG知识库向量队列
     *
     * @param stockCode 股票代码
     * @param stockName 股票名称
     * @param dataType 数据类型
     * @param title 标题
     * @param content 内容
     */
    private void sendToVectorQueue(String stockCode, String stockName, String dataType,
                                    String title, String content) {
        try {
            StockDataMessage message = StockDataMessage.builder()
                    .messageId(UUID.randomUUID().toString())
                    .stockCode(stockCode)
                    .stockName(stockName)
                    .dataType(dataType)
                    .title(title)
                    .content(content)
                    .publishTime(LocalDateTime.now())
                    .source(SOURCE_SINA)
                    .createTime(LocalDateTime.now())
                    .build();

            // 发送到RabbitMQ队列（使用统一配置）
            rabbitTemplate.convertAndSend(
                    rabbitMQProperties.getDataExchange(),
                    rabbitMQProperties.getVectorRoutingKey(),
                    message
            );

            log.debug("成功发送股票数据到向量队列: stockCode={}, dataType={}, messageId={}",
                    stockCode, dataType, message.getMessageId());

        } catch (Exception e) {
            log.error("发送股票数据到向量队列失败: stockCode={}, dataType={}, error={}",
                    stockCode, dataType, e.getMessage());
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 批量发送股票数据到RAG知识库向量队列
     *
     * @param messages 消息列表
     */
    private void batchSendToVectorQueue(List<StockDataMessage> messages) {
        try {
            for (StockDataMessage message : messages) {
                rabbitTemplate.convertAndSend(
                        rabbitMQProperties.getDataExchange(),
                        rabbitMQProperties.getVectorRoutingKey(),
                        message
                );
            }
            log.debug("成功批量发送股票数据到向量队列: count={}", messages.size());
        } catch (Exception e) {
            log.error("批量发送股票数据到向量队列失败: count={}, error={}",
                    messages.size(), e.getMessage());
        }
    }


    /**
     * 带重试机制的批量插入方法
     * 用于处理数据库死锁异常，当发生死锁时自动重试
     *
     * @param list 要插入的数据列表
     * @param maxRetries 最大重试次数
     * @return 插入成功的记录数
     */
    private int insertBatchWithRetry(List list, int maxRetries) {
        int retryCount = 0;
        DeadlockLoserDataAccessException lastException = null;

        while (retryCount <= maxRetries) {
            try {
                return stockRtInfoMapper.insertBatch(list);
            } catch (DeadlockLoserDataAccessException e) {
                lastException = e;
                retryCount++;

                if (retryCount <= maxRetries) {
                    // 指数退避策略：重试前等待一段时间
                    long waitTime = (long) (Math.random() * 100 * retryCount); // 100ms, 200ms, 300ms
                    log.warn("检测到数据库死锁，第 {} 次重试，等待 {}ms 后重试",
                            retryCount, waitTime);
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("重试等待被中断", ie);
                        return 0;
                    }
                } else {
                    log.error("达到最大重试次数 ({})，插入失败", maxRetries);
                }
            }
        }

        // 所有重试都失败后，记录最后一次异常
        if (lastException != null) {
            log.error("批量插入失败，已重试 {} 次: {}", maxRetries, lastException.getMessage());
        }
        return 0;
    }
}
