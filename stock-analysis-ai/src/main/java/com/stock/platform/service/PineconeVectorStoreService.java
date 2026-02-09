package com.stock.platform.service;

import com.me.stock.pojo.dto.StockDataMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pinecone向量存储服务
 * 负责将股票数据转换为文档并存入Pinecone向量数据库
 */
@Service
@Slf4j
public class PineconeVectorStoreService {

    @Autowired
    private VectorStore vectorStore;

    @Value("${spring.ai.vectorstore.pinecone.index-name:stock-analysis}")
    private String indexName;

    /**
     * 将股票数据消息存入向量数据库
     *
     * @param message 股票数据消息
     */
    public void storeStockData(StockDataMessage message) {
        try {
            log.info("开始存储股票数据到Pinecone: stockCode={}, dataType={}, title={}",
                    message.getStockCode(), message.getDataType(), message.getTitle());

            // 构建文档内容
            String documentContent = buildDocumentContent(message);

            // 构建元数据
            Map<String, Object> metadata = buildMetadata(message);

            // 创建Document对象
            Document document = new Document(
                    message.getMessageId(), // 使用messageId作为文档ID
                    documentContent,
                    metadata
            );

            // 存入向量数据库
            vectorStore.add(List.of(document));

            log.info("成功存储股票数据到Pinecone: messageId={}, stockCode={}, dataType={}",
                    message.getMessageId(), message.getStockCode(), message.getDataType());

        } catch (Exception e) {
            log.error("存储股票数据到Pinecone失败: messageId={}, stockCode={}, error={}",
                    message.getMessageId(), message.getStockCode(), e.getMessage(), e);
            throw new RuntimeException("向量存储失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量存储股票数据
     *
     * @param messages 股票数据消息列表
     */
    public void batchStoreStockData(List<StockDataMessage> messages) {
        try {
            log.info("开始批量存储股票数据到Pinecone: count={}", messages.size());

            List<Document> documents = messages.stream()
                    .map(message -> {
                        String content = buildDocumentContent(message);
                        Map<String, Object> metadata = buildMetadata(message);
                        return new Document(message.getMessageId(), content, metadata);
                    })
                    .toList();

            // 批量存入向量数据库
            vectorStore.add(documents);

            log.info("成功批量存储股票数据到Pinecone: count={}", messages.size());

        } catch (Exception e) {
            log.error("批量存储股票数据到Pinecone失败: count={}, error={}",
                    messages.size(), e.getMessage(), e);
            throw new RuntimeException("批量向量存储失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建文档内容
     * 将股票数据的各个字段组合成适合向量检索的文本内容
     */
    private String buildDocumentContent(StockDataMessage message) {
        StringBuilder content = new StringBuilder();

        // 添加标题
        if (message.getTitle() != null && !message.getTitle().isEmpty()) {
            content.append("标题: ").append(message.getTitle()).append("\n");
        }

        // 添加股票基本信息
        content.append("股票代码: ").append(message.getStockCode()).append("\n");
        if (message.getStockName() != null) {
            content.append("股票名称: ").append(message.getStockName()).append("\n");
        }

        // 添加数据类型
        content.append("数据类型: ").append(message.getDataType()).append("\n");

        // 添加主要内容
        if (message.getContent() != null && !message.getContent().isEmpty()) {
            content.append("\n").append(message.getContent()).append("\n");
        }

        // 添加来源信息
        if (message.getSource() != null) {
            content.append("\n来源: ").append(message.getSource());
        }

        return content.toString();
    }

    /**
     * 构建元数据
     * 元数据用于过滤和检索时的条件筛选
     */
    private Map<String, Object> buildMetadata(StockDataMessage message) {
        Map<String, Object> metadata = new HashMap<>();

        // 基本信息
        metadata.put("stockCode", message.getStockCode());
        if (message.getStockName() != null) {
            metadata.put("stockName", message.getStockName());
        }
        metadata.put("dataType", message.getDataType());

        // 时间信息
        if (message.getPublishTime() != null) {
            metadata.put("publishTime", message.getPublishTime().toString());
        }

        // 来源信息
        if (message.getSource() != null) {
            metadata.put("source", message.getSource());
        }

        // URL信息
        if (message.getUrl() != null) {
            metadata.put("url", message.getUrl());
        }

        // 扩展元数据
        if (message.getMetadata() != null && !message.getMetadata().isEmpty()) {
            metadata.putAll(message.getMetadata());
        }

        // 标签
        if (message.getTags() != null && message.getTags().length > 0) {
            metadata.put("tags", String.join(",", message.getTags()));
        }

        return metadata;
    }

    /**
     * 删除指定股票的向量数据
     *
     * @param stockCode 股票代码
     */
    public void deleteByStockCode(String stockCode) {
        try {
            log.info("开始删除股票向量数据: stockCode={}", stockCode);
            // 注意：Pinecone删除操作需要根据具体的Spring AI版本API进行调整
            // vectorStore.delete(List.of(stockCode));
            log.warn("删除操作暂未实现，需要根据Pinecone API文档补充");
        } catch (Exception e) {
            log.error("删除股票向量数据失败: stockCode={}, error={}", stockCode, e.getMessage(), e);
        }
    }
}
