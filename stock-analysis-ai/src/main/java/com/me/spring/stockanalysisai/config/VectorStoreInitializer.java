package com.me.spring.stockanalysisai.config;

import com.me.spring.stockanalysisai.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 向量存储初始化器
 * 在应用启动后初始化知识库文档到Pinecone
 *
 * @author system
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorStoreInitializer implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final KnowledgeBaseService knowledgeBaseService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // 从资源目录加载知识库文档
            List<Document> knowledgeDocuments = knowledgeBaseService.loadAllDocuments();

            // 将文档添加到Pinecone向量存储
            vectorStore.add(knowledgeDocuments);

            log.info("知识库初始化完成，已添加文档数量: {}", knowledgeDocuments.size());

        } catch (Exception e) {
            log.error("向量存储初始化失败", e);
            throw new RuntimeException("向量存储初始化失败", e);
        }
    }
}
