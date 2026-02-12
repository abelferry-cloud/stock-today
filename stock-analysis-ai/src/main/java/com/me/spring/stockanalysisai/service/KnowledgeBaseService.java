package com.me.spring.stockanalysisai.service;

import com.me.spring.stockanalysisai.pojo.response.KnowledgeBaseStatusVO;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * 知识库服务接口
 * 
 * @author system
 * @since 1.0.0
 */
public interface KnowledgeBaseService {

    /**
     * 加载系统提示词
     * 
     * @return 系统提示词内容
     */
    String loadSystemPrompt();

    /**
     * 加载所有知识库文档
     * 
     * @return 文档列表
     */
    List<Document> loadAllDocuments();

    /**
     * 获取知识库状态信息
     * 
     * @return 知识库状态
     */
    KnowledgeBaseStatusVO getKnowledgeBaseStatus();
}
