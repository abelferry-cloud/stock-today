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
 * 基于 Spring AI 的 VectorStore 实现
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
            // 注意：不是所有的 VectorStore 实现都支持删除所有文档
            // 这里需要根据具体的 VectorStore 实现来处理
            log.warn("清空向量存储功能需要根据具体 VectorStore 实现");
            return false;
        } catch (Exception e) {
            log.error("清空向量存储失败: error={}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getDocumentCount() {
        try {
            // 注意：不是所有的 VectorStore 实现都支持获取文档数量
            // 这里需要根据具体的 VectorStore 实现来处理
            log.warn("获取文档数量功能需要根据具体 VectorStore 实现");
            return -1;
        } catch (Exception e) {
            log.error("获取文档数量失败: error={}", e.getMessage(), e);
            return -1;
        }
    }
}
