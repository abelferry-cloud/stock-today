package com.me.spring.stockanalysisai.service.impl;

import com.me.spring.stockanalysisai.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 向量存储服务实现类
 * 基于 Spring AI 的 VectorStore 实现 (Pinecone)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {

    private final VectorStore vectorStore;

    @Override
    public boolean addDocument(Document document) {
        try {
            log.info("开始添加文档到向量存储: {}", document.getId());
            vectorStore.add(List.of(document));
            log.info("成功添加文档到向量存储: {}", document.getId());
            return true;
        } catch (Exception e) {
            log.error("添加文档到向量存储失败: documentId={}, error={}",
                    document.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int addDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            log.warn("文档列表为空，跳过添加操作");
            return 0;
        }

        try {
            log.info("开始批量添加 {} 个文档到向量存储", documents.size());
            vectorStore.add(documents);
            log.info("成功批量添加 {} 个文档到向量存储", documents.size());
            return documents.size();
        } catch (Exception e) {
            log.error("批量添加文档到向量存储失败: count={}, error={}",
                    documents.size(), e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean deleteDocument(String documentId) {
        try {
            log.info("开始删除文档从向量存储: {}", documentId);
            List<String> ids = List.of(documentId);
            vectorStore.delete(ids);
            log.info("成功删除文档从向量存储: {}", documentId);
            return true;
        } catch (Exception e) {
            log.error("删除文档从向量存储失败: documentId={}, error={}",
                    documentId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean clearAllDocuments() {
        try {
            log.warn("开始清空向量存储中的所有文档");
            // 注意：Pinecone不支持直接清空所有文档，需要通过API删除
            log.warn("清空向量存储功能需要通过Pinecone控制台操作");
            return false;
        } catch (Exception e) {
            log.error("清空向量存储失败: error={}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getDocumentCount() {
        try {
            // Pinecone不直接提供文档数量API，需要通过stats获取
            log.warn("获取文档数量功能需要通过Pinecone控制台查看");
            return -1;
        } catch (Exception e) {
            log.error("获取文档数量失败: error={}", e.getMessage(), e);
            return -1;
        }
    }
}
