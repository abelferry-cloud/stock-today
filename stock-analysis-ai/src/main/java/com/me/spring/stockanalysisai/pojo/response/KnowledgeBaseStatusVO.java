package com.me.spring.stockanalysisai.pojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识库状态VO
 * 
 * @author system
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库状态信息")
public class KnowledgeBaseStatusVO {

    /**
     * 文档总数
     */
    @Schema(description = "文档总数", example = "10")
    private Integer documentCount;

    /**
     * 系统提示词
     */
    @Schema(description = "系统提示词长度", example = "1234")
    private Integer systemPromptLength;

    /**
     * 知识库文档列表
     */
    @Schema(description = "知识库文档列表")
    private List<DocumentInfo> documents;

    /**
     * 文档信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文档信息")
    public static class DocumentInfo {
        
        /**
         * 文件名
         */
        @Schema(description = "文件名", example = "01_市盈率.txt")
        private String filename;
        
        /**
         * 标题
         */
        @Schema(description = "标题", example = "市盈率")
        private String title;
        
        /**
         * 分类
         */
        @Schema(description = "分类", example = "基础指标")
        private String category;
        
        /**
         * 关键词
         */
        @Schema(description = "关键词", example = "市盈率,PE,估值")
        private String keywords;
    }
}
