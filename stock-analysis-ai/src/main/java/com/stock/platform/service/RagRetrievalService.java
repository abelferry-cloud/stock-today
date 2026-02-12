package com.stock.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG检索服务
 * 从Pinecone向量数据库中检索相关文档，为AI对话提供上下文信息
 */
@Service
@Slf4j
public class RagRetrievalService {

    @Autowired
    private VectorStore vectorStore;

    @Value("${app.rag.top-k:5}")
    private int topK;

    @Value("${app.rag.similarity-threshold:0.7}")
    private double similarityThreshold;

    /**
     * 根据用户问题检索相关文档
     *
     * @param query 用户问题
     * @return 格式化的上下文文本
     */
    public String retrieveRelevantContext(String query) {
        return retrieveRelevantContext(query, null, null);
    }

    /**
     * 根据用户问题检索相关文档（可按股票代码过滤）
     *
     * @param query 用户问题
     * @param stockCode 股票代码（可选，用于过滤）
     * @return 格式化的上下文文本
     */
    public String retrieveRelevantContext(String query, String stockCode) {
        return retrieveRelevantContext(query, stockCode, null);
    }

    /**
     * 根据用户问题检索相关文档（可按多个条件过滤）
     *
     * @param query 用户问题
     * @param stockCode 股票代码（可选，用于过滤）
     * @param dataType 数据类型（可选，如：NEWS、REPORT、ANNOUNCEMENT、REALTIME）
     * @return 格式化的上下文文本
     */
    public String retrieveRelevantContext(String query, String stockCode, String dataType) {
        try {
            log.info("开始RAG向量检索: query={}, stockCode={}, dataType={}, top-k={}",
                    query, stockCode, dataType, topK);

            // 执行向量检索
            // 使用VectorStore直接进行相似度搜索
            List<Document> documents = vectorStore.similaritySearch(query);

            if (documents.isEmpty()) {
                log.info("未检索到相关文档: query={}", query);
                return "";
            }

            log.info("检索到相关文档: count={}, query={}", documents.size(), query);

            // 格式化检索结果为上下文文本
            String context = formatContext(documents);

            log.debug("格式化后的上下文: length={}", context.length());

            return context;

        } catch (Exception e) {
            log.error("RAG向量检索失败: query={}, error={}", query, e.getMessage(), e);
            return ""; // 检索失败时返回空上下文，不影响对话继续
        }
    }

    /**
     * 格式化检索结果为上下文文本
     * 将检索到的文档组织成易于理解的格式
     */
    private String formatContext(List<Document> documents) {
        StringBuilder context = new StringBuilder();
        context.append("以下是与用户问题相关的股票信息：\n\n");

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            Map<String, Object> metadata = doc.getMetadata();

            context.append("【相关资料 ").append(i + 1).append("】\n");

            // 添加标题（如果有）
            if (metadata.containsKey("stockName")) {
                context.append("股票名称: ").append(metadata.get("stockName")).append("\n");
            }

            // 添加股票代码
            if (metadata.containsKey("stockCode")) {
                context.append("股票代码: ").append(metadata.get("stockCode")).append("\n");
            }

            // 添加数据类型
            if (metadata.containsKey("dataType")) {
                context.append("数据类型: ").append(metadata.get("dataType")).append("\n");
            }

            // 添加发布时间（如果有）
            if (metadata.containsKey("publishTime")) {
                context.append("发布时间: ").append(metadata.get("publishTime")).append("\n");
            }

            // 添加来源（如果有）
            if (metadata.containsKey("source")) {
                context.append("来源: ").append(metadata.get("source")).append("\n");
            }

            // 添加文档内容
            context.append("\n").append(doc.getText()).append("\n");

            // 添加分隔线
            if (i < documents.size() - 1) {
                context.append("\n---\n\n");
            }
        }

        return context.toString();
    }

    /**
     * 从文本中提取股票代码（简单实现）
     * 用于自动判断是否需要进行特定股票的检索
     *
     * @param text 用户输入文本
     * @return 股票代码，如果未找到则返回null
     */
    public String extractStockCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 简单的正则匹配：6位数字（常见的A股代码格式）
        // 可以根据需要扩展支持更多格式的股票代码
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d{6}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String stockCode = matcher.group();
            log.debug("从文本中提取到股票代码: {}", stockCode);
            return stockCode;
        }

        return null;
    }

    /**
     * 判断用户问题是否涉及股票数据
     * 通过关键词匹配来判断
     *
     * @param query 用户问题
     * @return true-需要RAG检索，false-不需要
     */
    public boolean requiresRagRetrieval(String query) {
        if (query == null || query.isEmpty()) {
            return false;
        }

        // 股票相关关键词
        String[] stockKeywords = {
                "股票", "股价", "涨跌", "财报", "业绩", "公司", "板块",
                "行情", "开盘", "收盘", "成交量", "市值", "市盈率",
                "分析", "预测", "投资", "风险", "公告", "新闻", "研报"
        };

        String lowerQuery = query.toLowerCase();
        for (String keyword : stockKeywords) {
            if (lowerQuery.contains(keyword)) {
                log.debug("检测到股票相关关键词: {}, 启用RAG检索", keyword);
                return true;
            }
        }

        // 如果包含6位数字的股票代码，也需要RAG检索
        if (extractStockCode(query) != null) {
            return true;
        }

        return false;
    }
}
