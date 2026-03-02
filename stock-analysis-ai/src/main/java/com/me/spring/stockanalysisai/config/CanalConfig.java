package com.me.spring.stockanalysisai.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Canal 配置类
 * 监听 MySQL Binlog，捕获 stock_rt_info 表的变更并发送到 RabbitMQ
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "canal.server.host")
public class CanalConfig {

    @Value("${canal.server.host}")
    private String canalHost;

    @Value("${canal.server.port}")
    private int canalPort;

    @Value("${canal.destination}")
    private String destination;

    @Value("${canal.username}")
    private String username;

    @Value("${canal.password}")
    private String password;

    @Value("${stock.cache.invalid.exchange}")
    private String cacheInvalidExchange;

    @Value("${stock.cache.invalid.routing-key}")
    private String cacheInvalidRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private CanalConnector canalConnector;
    private ExecutorService executorService;

    public CanalConfig(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void startCanalClient() {
        try {
            log.info("Starting Canal client, connecting to {}:{}", canalHost, canalPort);

            // 创建 Canal 连接
            canalConnector = CanalConnectors.newSingleConnector(
                    new InetSocketAddress(canalHost, canalPort),
                    destination,
                    username,
                    password
            );
            canalConnector.connect();

            // 订阅 stock_rt_info 表的变更
            canalConnector.subscribe(".*\\.stock_rt_info");
            canalConnector.rollback();

            // 创建线程池处理消息
            executorService = Executors.newSingleThreadExecutor(runnable -> {
                Thread t = new Thread(runnable, "canal-client");
                t.setDaemon(true);
                return t;
            });

            // 启动消费线程
            executorService.submit(this::consumeCanalMessage);

            log.info("Canal client started successfully, subscribed to stock_rt_info table");

        } catch (Exception e) {
            log.error("Failed to start Canal client", e);
        }
    }

    /**
     * 消费 Canal 消息
     */
    private void consumeCanalMessage() {
        while (true) {
            try {
                // 获取消息（等待 1 秒）
                Message message = canalConnector.get(100);
                if (message == null || message.getEntries().isEmpty()) {
                    continue;
                }

                // 处理每个 entry
                for (CanalEntry.Entry entry : message.getEntries()) {
                    if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        processRowData(entry);
                    }
                }

                // 确认消息
                canalConnector.ack(message.getId());

            } catch (Exception e) {
                log.error("Error consuming Canal message", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 处理行数据变更
     */
    private void processRowData(CanalEntry.Entry entry) {
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                // 获取操作类型
                CanalEntry.EventType eventType = rowChange.getEventType();

                // 从 afterColumns 或 beforeColumns 获取股票代码
                String stockCode = extractStockCode(rowData, eventType);

                if (stockCode != null) {
                    // 发送缓存失效消息
                    sendCacheInvalidMessage(entry.getHeader().getTableName(), eventType.name(), stockCode);
                }
            }
        } catch (Exception e) {
            log.error("Error processing row data", e);
        }
    }

    /**
     * 从行数据中提取股票代码
     */
    private String extractStockCode(CanalEntry.RowData rowData, CanalEntry.EventType eventType) {
        // 优先从 afterColumns 获取（INSERT/UPDATE），其次从 beforeColumns 获取（DELETE）
        var columns = eventType == CanalEntry.EventType.DELETE
                ? rowData.getBeforeColumnsList()
                : rowData.getAfterColumnsList();

        for (CanalEntry.Column column : columns) {
            // 假设股票代码字段名为 stock_code 或 code
            if ("stock_code".equalsIgnoreCase(column.getName()) || "code".equalsIgnoreCase(column.getName())) {
                return column.getValue();
            }
        }
        return null;
    }

    /**
     * 发送缓存失效消息到 RabbitMQ
     */
    private void sendCacheInvalidMessage(String tableName, String operationType, String stockCode) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("tableName", tableName);
            message.put("operationType", operationType);
            message.put("stockCode", stockCode);
            message.put("timestamp", java.time.Instant.now().toString());

            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(cacheInvalidExchange, cacheInvalidRoutingKey, jsonMessage);

            log.info("Sent cache invalid message: table={}, operation={}, stockCode={}",
                    tableName, operationType, stockCode);

        } catch (Exception e) {
            log.error("Failed to send cache invalid message", e);
        }
    }

    @PreDestroy
    public void stopCanalClient() {
        log.info("Stopping Canal client");
        if (executorService != null) {
            executorService.shutdown();
        }
        if (canalConnector != null) {
            canalConnector.disconnect();
        }
    }
}