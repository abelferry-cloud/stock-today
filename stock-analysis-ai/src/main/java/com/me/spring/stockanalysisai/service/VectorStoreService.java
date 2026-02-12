package com.me.spring.stockanalysisai.service;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * 向量存储服务接口
 * 用于管理 RAG 知识库的文档向量存储
 */
public interface VectorStoreService {

    /**
     * 添加单个文档到向量存储
     *
     * @param document 要添加的文档
     * @return 是否添加成功
     */
    boolean addDocument(Document document);

    /**
     * 批量添加文档到向量存储
     *
     * @param documents 要添加的文档列表
     * @return 成功添加的文档数量
     */
    int addDocuments(List<Document> documents);

    /**
     * 删除文档从向量存储
     *
     * @param documentId 要删除的文档ID
     * @return 是否删除成功
     */
    boolean deleteDocument(String documentId);

    /**
     * 清空向量存储中的所有文档
     *
     * @return 是否清空成功
     */
    boolean clearAllDocuments();

    /**
     * 获取向量存储中的文档总数
     *
     * @return 文档总数
     */
    int getDocumentCount();
}
