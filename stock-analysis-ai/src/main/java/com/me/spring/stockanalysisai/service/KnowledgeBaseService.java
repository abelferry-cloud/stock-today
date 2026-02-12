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

    /**
     * 动态添加单个文档到知识库
     * 用于接收来自消息队列的实时股票数据
     *
     * @param document 要添加的文档
     * @return 是否添加成功
     */
    boolean addDynamicDocument(Document document);

    /**
     * 动态批量添加文档到知识库
     * 用于批量更新实时股票数据
     *
     * @param documents 要添加的文档列表
     * @return 成功添加的文档数量
     */
    int addDynamicDocuments(List<Document> documents);
}
