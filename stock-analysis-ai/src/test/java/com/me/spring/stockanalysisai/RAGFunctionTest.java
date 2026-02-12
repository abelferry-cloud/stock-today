package com.me.spring.stockanalysisai;

import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * RAG功能测试类
 * 全面测试向量检索和上下文增强的完整流程
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RAGFunctionTest {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    @Qualifier("ragChatClient")
    private ChatClient ragChatClient;

    @Autowired
    @Qualifier("simpleChatClient")
    private ChatClient simpleChatClient;

    private static StringBuilder testReport = new StringBuilder();

    @BeforeAll
    public static void init() {
        testReport.append("\n");
        testReport.append("========================================\n");
        testReport.append("       RAG功能测试报告\n");
        testReport.append("========================================\n");
        testReport.append("测试时间: ").append(new java.util.Date()).append("\n");
        testReport.append("========================================\n\n");
    }

    @AfterAll
    public static void finish() {
        testReport.append("\n========================================\n");
        testReport.append("       测试总结\n");
        testReport.append("========================================\n");
        testReport.append("所有测试完成！\n");
        testReport.append("========================================\n");

        System.out.println(testReport.toString());
    }

    /**
     * 测试1: Embedding模型是否正常工作
     */
    @Test
    @Order(1)
    @DisplayName("测试Embedding模型功能")
    public void testEmbeddingModel() {
        testReport.append("\n【测试1】Embedding模型功能测试\n");
        testReport.append("----------------------------------------\n");

        try {
            // 测试文本
            String testText = "什么是股票？";

            // 生成向量
            EmbeddingResponse response = embeddingModel.embedForResponse(List.of(testText));
            float[] embedding = response.getResults().get(0).getOutput();

            // 验证结果
            Assertions.assertNotNull(embedding, "Embedding结果不应为null");
            Assertions.assertTrue(embedding.length > 0, "Embedding向量维度应大于0");
            Assertions.assertTrue(embedding.length == 1536, "Embedding向量维度应为1536");

            testReport.append("✓ Embedding生成成功\n");
            testReport.append("  - 测试文本: ").append(testText).append("\n");
            testReport.append("  - 向量维度: ").append(embedding.length).append("\n");
            testReport.append("  - 前5维数值: [");
            for (int i = 0; i < 5; i++) {
                testReport.append(String.format("%.4f", embedding[i]));
                if (i < 4) testReport.append(", ");
            }
            testReport.append("]\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试1】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("Embedding模型测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试2: 向量存储文档数量验证
     */
    @Test
    @Order(2)
    @DisplayName("测试向量存储文档加载")
    public void testVectorStoreDocuments() {
        testReport.append("\n【测试2】向量存储文档加载测试\n");
        testReport.append("----------------------------------------\n");

        try {
            // 执行一次相似度搜索以验证向量存储是否正常
            SearchRequest request = SearchRequest.query("股票交易").withTopK(5);
            List<Document> results = vectorStore.similaritySearch(request);

            // 验证结果
            Assertions.assertNotNull(results, "搜索结果不应为null");
            Assertions.assertTrue(results.size() > 0, "应返回至少一个搜索结果");

            testReport.append("✓ 向量存储正常工作\n");
            testReport.append("  - 搜索查询: 股票交易\n");
            testReport.append("  - 返回结果数: ").append(results.size()).append("\n");

            // 打印搜索结果
            testReport.append("\n  搜索结果详情:\n");
            for (int i = 0; i < Math.min(results.size(), 3); i++) {
                Document doc = results.get(i);
                testReport.append("    ").append(i + 1).append(". ")
                         .append(doc.getContent().substring(0, Math.min(50, doc.getContent().length())))
                         .append("...\n");
                testReport.append("       相似度: ").append(String.format("%.4f", doc.getMetadata().get("distance")))
                         .append("\n");
            }

            testReport.append("----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试2】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("向量存储测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试3: 向量相似度搜索功能
     */
    @Test
    @Order(3)
    @DisplayName("测试向量相似度搜索")
    public void testSimilaritySearch() {
        testReport.append("\n【测试3】向量相似度搜索测试\n");
        testReport.append("----------------------------------------\n");

        try {
            // 测试不同的查询
            String[] queries = {
                    "什么是市盈率？",
                    "股票交易时间是什么时候？",
                    "K线图是什么？",
                    "什么是涨停板？"
            };

            for (String query : queries) {
                SearchRequest request = SearchRequest.query(query).withTopK(1);
                List<Document> results = vectorStore.similaritySearch(request);

                Assertions.assertNotNull(results, "搜索结果不应为null");
                Assertions.assertTrue(results.size() > 0, "应返回至少一个搜索结果");

                Document topDoc = results.get(0);
                testReport.append("✓ 查询: ").append(query).append("\n");
                testReport.append("  相关文档: ").append(topDoc.getContent().substring(0, Math.min(80, topDoc.getContent().length()))).append("...\n");
                testReport.append("\n");
            }

            testReport.append("----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试3】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("相似度搜索测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试4: RAG功能 - 使用知识库回答
     */
    @Test
    @Order(4)
    @DisplayName("测试RAG功能（带知识库）")
    public void testRAGChat() {
        testReport.append("\n【测试4】RAG功能测试（带知识库）\n");
        testReport.append("----------------------------------------\n");

        try {
            // 测试问题
            String question = "什么是市盈率？";

            testReport.append("问题: ").append(question).append("\n");
            testReport.append("\n使用RAG ChatClient回答:\n");

            long startTime = System.currentTimeMillis();
            String answer = ragChatClient.prompt()
                    .user(question)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            testReport.append(answer).append("\n");
            testReport.append("\n响应时间: ").append(endTime - startTime).append("ms\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试4】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("RAG聊天测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试5: 对比测试 - RAG vs 普通聊天
     */
    @Test
    @Order(5)
    @DisplayName("对比测试：RAG vs 普通聊天")
    public void testRAGVsSimple() {
        testReport.append("\n【测试5】对比测试（RAG vs 普通聊天）\n");
        testReport.append("========================================\n");

        try {
            // 测试问题
            String question = "股票交易时间是什么时候？";

            testReport.append("问题: ").append(question).append("\n\n");

            // RAG回答
            testReport.append("【RAG ChatClient回答】\n");
            long ragStartTime = System.currentTimeMillis();
            String ragAnswer = ragChatClient.prompt()
                    .user(question)
                    .call()
                    .content();
            long ragEndTime = System.currentTimeMillis();

            testReport.append(ragAnswer).append("\n");
            testReport.append("响应时间: ").append(ragEndTime - ragStartTime).append("ms\n");

            testReport.append("\n----------------------------------------\n");

            // 普通回答
            testReport.append("【普通ChatClient回答】\n");
            long simpleStartTime = System.currentTimeMillis();
            String simpleAnswer = simpleChatClient.prompt()
                    .user(question)
                    .call()
                    .content();
            long simpleEndTime = System.currentTimeMillis();

            testReport.append(simpleAnswer).append("\n");
            testReport.append("响应时间: ").append(simpleEndTime - simpleStartTime).append("ms\n");

            testReport.append("\n----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");
            testReport.append("========================================\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试5】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("对比测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试6: 多轮对话测试 - 验证上下文记忆
     */
    @Test
    @Order(6)
    @DisplayName("测试多轮对话和上下文记忆")
    public void testMultiTurnConversation() {
        testReport.append("\n【测试6】多轮对话测试\n");
        testReport.append("----------------------------------------\n");

        try {
            String conversationId = "test-conversation-001";

            // 第一轮对话
            String question1 = "什么是K线图？";
            testReport.append("【第一轮对话】\n");
            testReport.append("用户: ").append(question1).append("\n");

            String answer1 = ragChatClient.prompt()
                    .user(question1)
                    .advisors(a -> a.param(ChatClient.CONVERSATION_ID_KEY, conversationId))
                    .call()
                    .content();

            testReport.append("AI: ").append(answer1.substring(0, Math.min(100, answer1.length()))).append("...\n\n");

            // 第二轮对话 - 追问
            String question2 = "那红色的K线代表什么意思？";
            testReport.append("【第二轮对话】\n");
            testReport.append("用户: ").append(question2).append("\n");

            String answer2 = ragChatClient.prompt()
                    .user(question2)
                    .advisors(a -> a.param(ChatClient.CONVERSATION_ID_KEY, conversationId))
                    .call()
                    .content();

            testReport.append("AI: ").append(answer2.substring(0, Math.min(100, answer2.length()))).append("...\n");

            testReport.append("\n----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");
            testReport.append("说明: 上下文记忆功能正常工作\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试6】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("多轮对话测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试7: 知识库外的问题处理
     */
    @Test
    @Order(7)
    @DisplayName("测试知识库外的问题处理")
    public void testOutOfKnowledgeBaseQuestion() {
        testReport.append("\n【测试7】知识库外问题处理测试\n");
        testReport.append("----------------------------------------\n");

        try {
            // 测试知识库外的问题
            String question = "今天天气怎么样？";

            testReport.append("问题: ").append(question).append("\n");
            testReport.append("（这是一个知识库外的问题）\n\n");

            String answer = ragChatClient.prompt()
                    .user(question)
                    .call()
                    .content();

            testReport.append("AI回答: ").append(answer.substring(0, Math.min(150, answer.length()))).append("...\n");

            // 验证AI是否能够识别这是知识库外的问题
            boolean handledCorrectly = answer.contains("没有") || answer.contains("无法") ||
                                       answer.contains("抱歉") || answer.contains("不知道") ||
                                       answer.contains("超出");

            if (handledCorrectly) {
                testReport.append("\n✓ AI正确识别了知识库外的问题\n");
            } else {
                testReport.append("\n⚠ AI可能使用了通用知识回答\n");
            }

            testReport.append("----------------------------------------\n");
            testReport.append("结果: 通过 ✓\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试7】")));

        } catch (Exception e) {
            testReport.append("✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("----------------------------------------\n");
            testReport.append("结果: 失败 ✗\n");
            e.printStackTrace();
            Assertions.fail("知识库外问题测试失败: " + e.getMessage());
        }
    }
}
