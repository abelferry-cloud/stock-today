package com.stock.platform.service;

/**
 * RAG检索服务接口
 * 从Pinecone向量数据库中检索相关文档，为AI对话提供上下文信息
 */
public interface RagRetrievalService {

    /**
     * 根据用户问题检索相关文档
     *
     * @param query 用户问题
     * @return 格式化的上下文文本
     */
    String retrieveRelevantContext(String query);

    /**
     * 根据用户问题检索相关文档（可按股票代码过滤）
     *
     * @param query 用户问题
     * @param stockCode 股票代码（可选，用于过滤）
     * @return 格式化的上下文文本
     */
    String retrieveRelevantContext(String query, String stockCode);

    /**
     * 根据用户问题检索相关文档（可按多个条件过滤）
     *
     * @param query 用户问题
     * @param stockCode 股票代码（可选，用于过滤）
     * @param dataType 数据类型（可选，如：NEWS、REPORT、ANNOUNCEMENT、REALTIME）
     * @return 格式化的上下文文本
     */
    String retrieveRelevantContext(String query, String stockCode, String dataType);

    /**
     * 从文本中提取股票代码（简单实现）
     * 用于自动判断是否需要进行特定股票的检索
     *
     * @param text 用户输入文本
     * @return 股票代码，如果未找到则返回null
     */
    String extractStockCode(String text);

    /**
     * 判断用户问题是否涉及股票数据
     * 通过关键词匹配来判断
     *
     * @param query 用户问题
     * @return true-需要RAG检索，false-不需要
     */
    boolean requiresRagRetrieval(String query);
}
