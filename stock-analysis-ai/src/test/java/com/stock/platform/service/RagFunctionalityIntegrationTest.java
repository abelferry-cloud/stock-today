package com.stock.platform.service;

import com.me.stock.pojo.dto.StockDataMessage;
import com.stock.platform.config.TestRagConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RAG功能集成测试
 * <p>
 * 测试目标：验证RAG（检索增强生成）功能是否正常工作
 * 1. 向Pinecone向量数据库存储测试数据
 * 2. 通过用户查询检索相关文档
 * 3. 验证检索到的上下文包含预期的测试数据
 * <p>
 * 注意：
 * - 测试使用非真实股票代码（999999）避免污染生产数据
 * - 测试数据使用UUID作为唯一标识
 * - Pinecone索引需要1-10秒的时间，测试中包含等待逻辑
 * - 测试数据会保留在Pinecone中，可通过TEST-前缀识别
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRagConfiguration.class)
@Slf4j
class RagFunctionalityIntegrationTest {

    @Autowired
    private PineconeVectorStoreService pineconeVectorStoreService;

    @Autowired
    private RagRetrievalService ragRetrievalService;

    @Autowired
    private VectorStore vectorStore;

    @BeforeEach
    void setUp() {
        // 验证VectorStore已正确初始化
        assertThat(vectorStore).isNotNull();
        log.info("========================================");
        log.info("RAG功能集成测试开始");
        log.info("VectorStore已成功初始化");
        log.info("========================================");
    }

    @Test
    @DisplayName("应该能够向Pinecone存储测试文档并成功检索")
    void testStoreAndRetrieveDocument() throws InterruptedException {
        // ========================================
        // Arrange: 准备测试数据
        // ========================================
        String testMessageId = "TEST-" + UUID.randomUUID();
        String testStockCode = "999999";  // 使用非真实股票代码
        String testStockName = "测试股票公司";
        String testTitle = "测试数据标题";
        String testContent = "这是一条测试数据内容，用于验证RAG功能是否正常工作。" +
                "包含关键词：股票、价格、涨跌幅。当前价格为100.50元，" +
                "今日涨幅为5.2%，市值为500亿元。该公司主要从事人工智能和" +
                "云计算业务，2024年第三季度财报显示营收同比增长35%。";

        StockDataMessage testMessage = StockDataMessage.builder()
                .messageId(testMessageId)
                .stockCode(testStockCode)
                .stockName(testStockName)
                .dataType("TEST_DATA")
                .title(testTitle)
                .content(testContent)
                .publishTime(LocalDateTime.now())
                .source("TEST_SOURCE")
                .build();

        log.info("准备测试数据: messageId={}, stockCode={}, stockName={}",
                testMessageId, testStockCode, testStockName);

        // ========================================
        // Act: 存储到Pinecone
        // ========================================
        log.info("开始存储测试文档到Pinecone...");
        pineconeVectorStoreService.storeStockData(testMessage);
        log.info("测试文档已成功存储: messageId={}", testMessageId);

        // 等待Pinecone索引完成（Pinecone通常需要1-10秒）
        log.info("等待Pinecone索引完成（5秒）...");
        Thread.sleep(5000);

        // ========================================
        // Act: 使用相关问题查询
        // ========================================
        String query = "999999股票的最新价格和涨跌幅是多少？";
        log.info("执行RAG检索查询: query={}", query);

        String retrievedContext = ragRetrievalService.retrieveRelevantContext(query, testStockCode);

        log.info("========================================");
        log.info("检索到的上下文内容:");
        log.info("========================================");
        log.info(retrievedContext);
        log.info("========================================");

        // ========================================
        // Assert: 验证检索结果包含预期内容
        // ========================================
        log.info("开始验证检索结果...");

        assertThat(retrievedContext)
                .as("检索到的上下文不应为空")
                .isNotEmpty();

        assertThat(retrievedContext)
                .as("应该包含股票名称: " + testStockName)
                .contains(testStockName);

        assertThat(retrievedContext)
                .as("应该包含数据标题: " + testTitle)
                .contains(testTitle);

        assertThat(retrievedContext)
                .as("应该包含价格信息: 100.50")
                .contains("100.50");

        assertThat(retrievedContext)
                .as("应该包含涨跌幅信息: 5.2%")
                .contains("5.2%");

        assertThat(retrievedContext)
                .as("应该包含市值信息")
                .contains("500亿元");

        log.info("所有断言通过！RAG功能验证成功！");
        log.info("测试数据已保留在Pinecone中，可通过ID {} 查看详情", testMessageId);
    }

    @Test
    @DisplayName("应该能够检测到股票相关关键词并启用RAG检索")
    void testKeywordDetection() {
        log.info("测试关键词检测功能...");

        // 测试各种股票相关关键词
        String[] stockRelatedQueries = {
                "今天股价如何？",
                "分析一下财报",
                "有什么投资建议吗",
                "这只股票的市盈率是多少",
                "板块行情怎么样"
        };

        for (String query : stockRelatedQueries) {
            boolean result = ragRetrievalService.requiresRagRetrieval(query);
            log.info("查询: [{}] -> 需要RAG检索: {}", query, result);
            assertThat(result)
                    .as("查询应该触发RAG检索: " + query)
                    .isTrue();
        }

        // 测试非股票相关查询
        String[] nonStockQueries = {
                "今天天气怎么样",
                "Python如何学习",
                "晚饭吃什么"
        };

        for (String query : nonStockQueries) {
            boolean result = ragRetrievalService.requiresRagRetrieval(query);
            log.info("查询: [{}] -> 需要RAG检索: {}", query, result);
            assertThat(result)
                    .as("查询不应触发RAG检索: " + query)
                    .isFalse();
        }

        log.info("关键词检测功能测试通过！");
    }

    @Test
    @DisplayName("应该能够从文本中提取6位数字的股票代码")
    void testStockCodeExtraction() {
        log.info("测试股票代码提取功能...");

        // 测试用例：输入文本和期望的股票代码
        String[][] testCases = {
                {"我想查询000001股票的行情", "000001"},
                {"600519这只股票怎么样", "600519"},
                {"分析一下300059的财报数据", "300059"},
                {"今天股市表现不错", null},  // 没有股票代码
                {"我想买股票，代码是123456", "123456"}
        };

        for (String[] testCase : testCases) {
            String text = testCase[0];
            String expectedCode = testCase[1];

            String extractedCode = ragRetrievalService.extractStockCode(text);
            log.info("输入: [{}] -> 提取到的股票代码: [{}]", text, extractedCode);

            assertThat(extractedCode)
                    .as("提取的股票代码应该匹配预期")
                    .isEqualTo(expectedCode);
        }

        log.info("股票代码提取功能测试通过！");
    }

    @Test
    @DisplayName("应该能够通过多个相似查询检索到相同的测试数据")
    void testMultipleSimilarQueries() throws InterruptedException {
        // ========================================
        // Arrange: 创建唯一的测试数据
        // ========================================
        String testMessageId = "TEST-MULTI-" + UUID.randomUUID();
        String testStockCode = "888888";

        StockDataMessage testMessage = StockDataMessage.builder()
                .messageId(testMessageId)
                .stockCode(testStockCode)
                .stockName("多查询测试公司")
                .dataType("TEST_DATA")
                .title("科技公司财报分析")
                .content("该公司是一家专注于人工智能和机器学习领域的科技公司。" +
                        "2024年Q3季度财报显示：营收25亿元，同比增长40%；" +
                        "净利润5.2亿元，同比增长55%；研发投入占比25%。")
                .publishTime(LocalDateTime.now())
                .source("TEST_SOURCE")
                .build();

        // ========================================
        // Act: 存储测试数据
        // ========================================
        log.info("存储测试文档用于多查询测试...");
        pineconeVectorStoreService.storeStockData(testMessage);
        Thread.sleep(5000);  // 等待索引完成

        // ========================================
        // Act: 使用多个不同的相似查询
        // ========================================
        String[] queries = {
                "888888公司的营收情况如何",
                "这家科技公司的净利润增长多少",
                "分析一下888888的研发投入",
                "人工智能公司的财报表现怎么样"
        };

        for (String query : queries) {
            log.info("执行查询: [{}]", query);
            String context = ragRetrievalService.retrieveRelevantContext(query, testStockCode);

            log.info("检索结果: {}", context);

            // 验证检索结果包含预期关键词
            assertThat(context)
                    .as("查询应该返回相关上下文: " + query)
                    .isNotEmpty();

            assertThat(context)
                    .as("应该包含公司名称或相关信息")
                    .containsAnyOf("多查询测试公司", "888888", "科技公司");
        }

        log.info("多查询测试通过！");
    }
}
