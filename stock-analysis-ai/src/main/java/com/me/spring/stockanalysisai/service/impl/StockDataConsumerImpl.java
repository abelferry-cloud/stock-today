package com.me.spring.stockanalysisai.service.impl;

import com.me.spring.stockanalysisai.service.StockDataConsumer;
import com.me.stock.pojo.dto.StockDataMessage;
import com.me.spring.stockanalysisai.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 股票数据消费者实现类
 * 负责消费来自 stock-crawler 的股票数据，并将其转换为 RAG 知识库文档格式
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockDataConsumerImpl implements StockDataConsumer {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 消息ID时间格式
     */
    private static final DateTimeFormatter MESSAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean consumeStockData(StockDataMessage message) {
        if (message == null) {
            log.warn("收到空消息，跳过处理");
            return false;
        }

        try {
            log.info("开始处理股票数据消息: messageId={}, dataType={}, stockCode={}, stockName={}",
                    message.getMessageId(), message.getDataType(), message.getStockCode(), message.getStockName());

            // 验证消息内容
            if (!validateMessage(message)) {
                log.warn("消息验证失败，跳过处理: messageId={}", message.getMessageId());
                return false;
            }

            // 转换为 RAG 文档格式
            Document document = convertToDocument(message);

            // 添加到知识库
            boolean success = knowledgeBaseService.addDynamicDocument(document);

            if (success) {
                log.info("成功处理并添加股票数据到知识库: messageId={}, stockCode={}, dataType={}",
                        message.getMessageId(), message.getStockCode(), message.getDataType());
            } else {
                log.error("添加股票数据到知识库失败: messageId={}, stockCode={}",
                        message.getMessageId(), message.getStockCode());
            }

            return success;

        } catch (Exception e) {
            log.error("处理股票数据消息异常: messageId={}, error={}",
                    message.getMessageId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证消息内容
     */
    private boolean validateMessage(StockDataMessage message) {
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            log.warn("消息ID为空");
            return false;
        }

        if (message.getStockCode() == null || message.getStockCode().isEmpty()) {
            log.warn("股票代码为空");
            return false;
        }

        if (message.getDataType() == null || message.getDataType().isEmpty()) {
            log.warn("数据类型为空");
            return false;
        }

        if (message.getContent() == null || message.getContent().isEmpty()) {
            log.warn("消息内容为空");
            return false;
        }

        // 验证数据类型是否合法
        if (!isValidDataType(message.getDataType())) {
            log.warn("无效的数据类型: {}", message.getDataType());
            return false;
        }

        return true;
    }

    /**
     * 检查数据类型是否合法
     */
    private boolean isValidDataType(String dataType) {
        return "INNER_MARKET".equals(dataType) ||
               "OUTER_MARKET".equals(dataType) ||
               "STOCK_RT".equals(dataType) ||
               "BLOCK_SECTOR".equals(dataType);
    }

    /**
     * 将 StockDataMessage 转换为 RAG 文档格式
     * 优化文档结构，使其更适合 RAG 检索
     */
    private Document convertToDocument(StockDataMessage message) {
        // 生成文档ID（使用消息ID作为唯一标识）
        String documentId = generateDocumentId(message);

        // 构建文档内容（优化为更适合 RAG 检索的格式）
        String content = buildDocumentContent(message);

        // 构建元数据（包含所有重要信息，用于检索过滤）
        Map<String, Object> metadata = buildMetadata(message);

        log.debug("构建文档完成: documentId={}, title={}", documentId, metadata.get("title"));

        return new Document(documentId, content, metadata);
    }

    /**
     * 生成文档ID
     */
    private String generateDocumentId(StockDataMessage message) {
        // 使用消息ID，或者生成新的唯一ID
        if (message.getMessageId() != null && !message.getMessageId().isEmpty()) {
            return message.getMessageId();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 构建文档内容
     * 将股票数据组织成结构化的文本格式，便于 AI 理解和检索
     */
    private String buildDocumentContent(StockDataMessage message) {
        StringBuilder content = new StringBuilder();

        // 添加标题
        if (message.getTitle() != null && !message.getTitle().isEmpty()) {
            content.append("【").append(message.getTitle()).append("】\n\n");
        }

        // 添加数据类型说明
        content.append("数据类型：").append(getDataTypeDescription(message.getDataType())).append("\n");

        // 添加基本信息
        content.append("股票代码：").append(message.getStockCode()).append("\n");
        if (message.getStockName() != null && !message.getStockName().isEmpty()) {
            content.append("股票名称：").append(message.getStockName()).append("\n");
        }

        // 添加数据来源和时间
        if (message.getSource() != null && !message.getSource().isEmpty()) {
            content.append("数据来源：").append(message.getSource()).append("\n");
        }

        if (message.getPublishTime() != null) {
            content.append("发布时间：").append(message.getPublishTime().format(MESSAGE_TIME_FORMAT)).append("\n");
        }

        // 添加空行分隔
        content.append("\n");

        // 添加详细内容
        if (message.getContent() != null && !message.getContent().isEmpty()) {
            content.append("详细信息：\n");
            content.append(message.getContent()).append("\n");
        }

        // 添加标签（如果有）
        if (message.getTags() != null && message.getTags().length > 0) {
            content.append("\n标签：");
            for (String tag : message.getTags()) {
                content.append(tag).append(" ");
            }
            content.append("\n");
        }

        // 添加URL（如果有）
        if (message.getUrl() != null && !message.getUrl().isEmpty()) {
            content.append("\n参考链接：").append(message.getUrl()).append("\n");
        }

        return content.toString();
    }

    /**
     * 获取数据类型的中文描述
     */
    private String getDataTypeDescription(String dataType) {
        return switch (dataType) {
            case "INNER_MARKET" -> "国内大盘指数";
            case "OUTER_MARKET" -> "国外大盘指数";
            case "STOCK_RT" -> "个股实时行情";
            case "BLOCK_SECTOR" -> "板块实时行情";
            default -> dataType;
        };
    }

    /**
     * 构建文档元数据
     * 包含所有用于检索和过滤的字段
     */
    private Map<String, Object> buildMetadata(StockDataMessage message) {
        Map<String, Object> metadata = new HashMap<>();

        // 基本信息
        metadata.put("stockCode", message.getStockCode());
        metadata.put("stockName", message.getStockName() != null ? message.getStockName() : "");
        metadata.put("dataType", message.getDataType());
        metadata.put("title", message.getTitle() != null ? message.getTitle() :
                getDefaultTitle(message.getDataType(), message.getStockCode(), message.getStockName()));

        // 来源和时间
        metadata.put("source", message.getSource() != null ? message.getSource() : "未知来源");
        metadata.put("publishTime", message.getPublishTime() != null ?
                message.getPublishTime().toString() : LocalDateTime.now().toString());
        metadata.put("createTime", message.getCreateTime() != null ?
                message.getCreateTime().toString() : LocalDateTime.now().toString());

        // 扩展字段（如果有）
        if (message.getMetadata() != null && !message.getMetadata().isEmpty()) {
            metadata.putAll(message.getMetadata());
        }

        // 标签
        if (message.getTags() != null && message.getTags().length > 0) {
            metadata.put("tags", String.join(",", message.getTags()));
        }

        // URL
        if (message.getUrl() != null && !message.getUrl().isEmpty()) {
            metadata.put("url", message.getUrl());
        }

        return metadata;
    }

    /**
     * 获取默认标题
     */
    private String getDefaultTitle(String dataType, String stockCode, String stockName) {
        String typeName = getDataTypeDescription(dataType);
        if (stockName != null && !stockName.isEmpty()) {
            return String.format("%s - %s（%s）", typeName, stockName, stockCode);
        } else {
            return String.format("%s - %s", typeName, stockCode);
        }
    }
}
