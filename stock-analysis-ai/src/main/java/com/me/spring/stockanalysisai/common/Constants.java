package com.me.spring.stockanalysisai.common;

/**
 * 全局常量定义类
 * 
 * @author system
 * @since 1.0.0
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 系统提示词文件路径
     */
    public static final String SYSTEM_PROMPT_PATH = "classpath:static/system-prompt.md";

    /**
     * 知识库元数据文件路径
     */
    public static final String METADATA_PATH = "classpath:static/knowledge/metadata.json";

    /**
     * 知识库文档路径前缀
     */
    public static final String KNOWLEDGE_PATH_PREFIX = "classpath:static/knowledge/";

    /**
     * UTF-8编码
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 向量相似度阈值
     */
    public static final double SIMILARITY_THRESHOLD = 0.8;

    /**
     * 向量检索返回数量
     */
    public static final int TOP_K = 6;

    /**
     * 聊天历史记录最大数量
     */
    public static final int MAX_HISTORY_MESSAGES = 10;

    /**
     * RAG检索相似度阈值
     */
    public static final double RAG_SIMILARITY_THRESHOLD = 0.50;
}
