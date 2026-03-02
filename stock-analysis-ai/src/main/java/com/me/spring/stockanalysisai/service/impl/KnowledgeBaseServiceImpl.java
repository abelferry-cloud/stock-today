package com.me.spring.stockanalysisai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.spring.stockanalysisai.common.Constants;
import com.me.spring.stockanalysisai.pojo.response.KnowledgeBaseStatusVO;
import com.me.spring.stockanalysisai.service.KnowledgeBaseService;
import com.me.spring.stockanalysisai.service.VectorStoreService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 * 
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final VectorStoreService vectorStoreService;

    @Override
    public String loadSystemPrompt() {
        try {
            Resource systemPromptResource = resourceLoader.getResource(Constants.SYSTEM_PROMPT_PATH);
            String systemPrompt = new String(
                    systemPromptResource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            // 清理Markdown格式，提取纯文本内容

            return cleanMarkdownToText(systemPrompt);
            
        } catch (IOException e) {
            log.error("加载系统提示词失败", e);
            throw new RuntimeException("加载系统提示词失败", e);
        }
    }

    @Override
    public List<Document> loadAllDocuments() {
        try {
            List<Document> documents = new ArrayList<>();

            // 检查知识库目录是否有文件
            if (!hasKnowledgeFiles()) {
                log.info("知识库目录为空，跳过加载静态文档");
                return documents;
            }

            // 加载元数据
            List<KnowledgeMetadata> metadataList = loadMetadata();
            // 根据元数据加载每个文档
            for (KnowledgeMetadata metadata : metadataList) {
                Document document = loadDocument(metadata);
                documents.add(document);
                log.debug("加载文档: {}", metadata.getTitle());
            }

            return documents;

        } catch (Exception e) {
            log.error("加载知识库文档失败", e);
            throw new RuntimeException("加载知识库文档失败", e);
        }
    }

    @Override
    public KnowledgeBaseStatusVO getKnowledgeBaseStatus() {
        try {
            log.info("获取知识库状态信息");

            // 加载系统提示词
            String systemPrompt = loadSystemPrompt();

            // 检查知识库目录是否有文件
            List<KnowledgeMetadata> metadataList;
            List<KnowledgeBaseStatusVO.DocumentInfo> documentInfos;

            if (hasKnowledgeFiles()) {
                metadataList = loadMetadata();
                documentInfos = metadataList.stream()
                        .map(metadata -> KnowledgeBaseStatusVO.DocumentInfo.builder()
                                .filename(metadata.getFilename())
                                .title(metadata.getTitle())
                                .category(metadata.getCategory())
                                .keywords(metadata.getKeywords())
                                .build())
                        .collect(Collectors.toList());
            } else {
                metadataList = Collections.emptyList();
                documentInfos = Collections.emptyList();
                log.info("知识库目录为空");
            }

            return KnowledgeBaseStatusVO.builder()
                    .documentCount(metadataList.size())
                    .systemPromptLength(systemPrompt.length())
                    .documents(documentInfos)
                    .build();

        } catch (Exception e) {
            log.error("获取知识库状态失败", e);
            throw new RuntimeException("获取知识库状态失败", e);
        }
    }

    @Override
    public boolean addDynamicDocument(Document document) {
        try {
            log.info("开始动态添加文档到知识库: documentId={}", document.getId());

            // 将文档添加到向量存储
            boolean success = vectorStoreService.addDocument(document);

            if (success) {
                log.info("成功动态添加文档到知识库: documentId={}, title={}",
                        document.getId(), document.getMetadata().get("title"));
            } else {
                log.warn("动态添加文档到知识库失败: documentId={}", document.getId());
            }

            return success;
        } catch (Exception e) {
            log.error("动态添加文档到知识库异常: documentId={}, error={}",
                    document.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int addDynamicDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            log.warn("文档列表为空，跳过动态添加操作");
            return 0;
        }

        try {
            log.info("开始动态批量添加 {} 个文档到知识库", documents.size());

            // 将文档列表添加到向量存储
            int successCount = vectorStoreService.addDocuments(documents);

            log.info("动态批量添加文档完成: 成功 {}/{}", successCount, documents.size());

            return successCount;
        } catch (Exception e) {
            log.error("动态批量添加文档到知识库异常: count={}, error={}",
                    documents.size(), e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 检查知识库目录是否有文件
     */
    private boolean hasKnowledgeFiles() {
        Resource metadataResource = resourceLoader.getResource(Constants.METADATA_PATH);
        return metadataResource.exists();
    }

    /**
     * 加载元数据文件
     */
    private List<KnowledgeMetadata> loadMetadata() throws IOException {
        Resource metadataResource = resourceLoader.getResource(Constants.METADATA_PATH);

        KnowledgeMetadata[] metadataArray = objectMapper.readValue(
                metadataResource.getInputStream(),
                KnowledgeMetadata[].class
        );

        return List.of(metadataArray);
    }

    /**
     * 加载单个文档
     */
    private Document loadDocument(KnowledgeMetadata metadata) throws IOException {
        Resource documentResource = resourceLoader.getResource(
                Constants.KNOWLEDGE_PATH_PREFIX + metadata.getFilename()
        );

        String content = new String(
                documentResource.getInputStream().readAllBytes(), 
                StandardCharsets.UTF_8
        );

        return new Document(
                content,
                Map.of(
                        "title", metadata.getTitle(),
                        "category", metadata.getCategory(),
                        "keywords", metadata.getKeywords(),
                        "filename", metadata.getFilename()
                )
        );
    }

    /**
     * 清理Markdown格式，提取纯文本内容用于系统提示词
     * 移除标题标记和格式符号，保留核心内容
     */
    private String cleanMarkdownToText(String markdown) {
        // 移除标题标记（##）
        String text = markdown.replaceAll("(?m)^#{1,6}\\s+", "");
        
        // 移除粗体标记（**）
        text = text.replaceAll("\\*\\*", "");
        
        // 移除斜体标记（*）
        text = text.replaceAll("(?<!\\*)\\*(?!\\*)", "");
        
        // 移除代码块标记（```）
        text = text.replaceAll("```[a-z]*\\n?", "");
        text = text.replaceAll("```", "");
        
        // 移除分隔线（---）
        text = text.replaceAll("---", "");
        
        // 压缩多个空行为单个空行
        text = text.replaceAll("\\n{3,}", "\n\n");
        
        // 移除首尾空白
        text = text.trim();
        
        return text;
    }

    /**
     * 知识库元数据类
     */
    @Data
    private static class KnowledgeMetadata {
        private String filename;
        private String title;
        private String category;
        private String keywords;
    }
}
