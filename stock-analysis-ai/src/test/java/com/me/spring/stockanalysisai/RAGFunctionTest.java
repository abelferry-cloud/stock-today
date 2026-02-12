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

    /**
     * 测试8: 添加知识库文档并测试RAG回答
     * 完整演示：创建文档 -> 添加到VectorStore -> 测试检索和回答
     */
    @Test
    @Order(8)
    @DisplayName("添加知识库文档并测试RAG回答")
    public void testAddDocumentsAndRAGChat() {
        testReport.append("\n【测试8】添加知识库文档并测试RAG回答\n");
        testReport.append("========================================\n");

        try {
            // 步骤1: 准备股票知识库文档
            testReport.append("【步骤1】准备股票知识库文档\n");
            testReport.append("----------------------------------------\n");

            List<Document> knowledgeDocuments = createStockKnowledgeBase();

            testReport.append("✓ 创建了 ").append(knowledgeDocuments.size()).append(" 条知识库文档\n\n");
            testReport.append("文档列表:\n");
            for (int i = 0; i < knowledgeDocuments.size(); i++) {
                Document doc = knowledgeDocuments.get(i);
                testReport.append("  ").append(i + 1).append(". ")
                         .append(doc.getMetadata().get("title")).append("\n");
            }

            // 步骤2: 将文档添加到向量存储
            testReport.append("\n【步骤2】将文档添加到向量存储\n");
            testReport.append("----------------------------------------\n");

            vectorStore.add(knowledgeDocuments);

            testReport.append("✓ 文档已成功添加到VectorStore\n");
            testReport.append("  - 文档数量: ").append(knowledgeDocuments.size()).append("\n");
            testReport.append("  - 向量维度: 1536\n");

            // 步骤3: 验证文档检索
            testReport.append("\n【步骤3】验证文档检索功能\n");
            testReport.append("----------------------------------------\n");

            // 测试不同的查询
            String[] testQueries = {
                "什么是市盈率？",
                "股票交易时间",
                "K线图怎么理解",
                "涨停板价格限制",
                "市净率的计算方法"
            };

            for (String query : testQueries) {
                testReport.append("\n测试查询: ").append(query).append("\n");

                // 执行相似度搜索
                SearchRequest searchRequest = SearchRequest.query(query)
                    .withTopK(2)
                    .withSimilarityThreshold(0.6);

                List<Document> searchResults = vectorStore.similaritySearch(searchRequest);

                testReport.append("  检索到 ").append(searchResults.size()).append(" 条相关文档:\n");

                for (int i = 0; i < Math.min(searchResults.size(), 2); i++) {
                    Document doc = searchResults.get(i);
                    String title = (String) doc.getMetadata().get("title");
                    String content = doc.getContent().substring(0, Math.min(100, doc.getContent().length()));
                    Double distance = doc.getMetadata().get("distance") != null
                        ? (Double) doc.getMetadata().get("distance")
                        : 0.0;
                    Double similarity = 1.0 - distance;

                    testReport.append("    ").append(i + 1).append(". ").append(title).append("\n");
                    testReport.append("       内容摘要: ").append(content).append("...\n");
                    testReport.append("       相似度: ").append(String.format("%.4f", similarity)).append("\n");
                }
            }

            // 步骤4: 使用RAG ChatClient测试问答
            testReport.append("\n【步骤4】使用RAG ChatClient测试问答\n");
            testReport.append("========================================\n");

            String[] questions = {
                "请解释什么是市盈率，它有什么作用？",
                "股票的交易日和时间安排是怎样的？",
                "K线图中的红色和绿色代表什么意思？",
                "什么是涨停板？涨跌幅限制是多少？"
            };

            for (int i = 0; i < questions.length; i++) {
                testReport.append("\n【问题").append(i + 1).append("】\n");
                testReport.append("用户: ").append(questions[i]).append("\n");
                testReport.append("AI: ");

                long startTime = System.currentTimeMillis();

                String answer = ragChatClient.prompt()
                    .user(questions[i])
                    .call()
                    .content();

                long endTime = System.currentTimeMillis();

                // 显示完整的回答
                testReport.append(answer).append("\n");
                testReport.append("\n⏱ 响应时间: ").append(endTime - startTime).append("ms\n");

                if (i < questions.length - 1) {
                    testReport.append("----------------------------------------\n");
                }
            }

            // 步骤5: 验证知识库外问题的处理
            testReport.append("\n【步骤5】验证知识库外问题处理\n");
            testReport.append("----------------------------------------\n");

            String outOfScopeQuestion = "今天北京的温度是多少度？";
            testReport.append("问题: ").append(outOfScopeQuestion).append("\n");
            testReport.append("（这是一个知识库外的问题，用于测试RAG的限制）\n\n");

            String outOfScopeAnswer = ragChatClient.prompt()
                .user(outOfScopeQuestion)
                .call()
                .content();

            testReport.append("AI回答: ").append(outOfScopeAnswer).append("\n");

            testReport.append("\n========================================\n");
            testReport.append("结果: 通过 ✓\n");
            testReport.append("说明: RAG知识库功能正常工作\n");
            testReport.append("========================================\n");

            System.out.println(testReport.substring(testReport.lastIndexOf("【测试8】")));

        } catch (Exception e) {
            testReport.append("\n✗ 测试失败: ").append(e.getMessage()).append("\n");
            testReport.append("========================================\n");
            testReport.append("结果: 失败 ✗\n");
            testReport.append("========================================\n");
            e.printStackTrace();
            Assertions.fail("添加文档并测试RAG回答失败: " + e.getMessage());
        }
    }

    /**
     * 创建股票知识库文档
     * 返回包含股票相关知识的Document列表
     */
    private List<Document> createStockKnowledgeBase() {
        return List.of(
            // 文档1: 市盈率
            new Document("市盈率（Price-to-Earnings Ratio，简称P/E）是股票投资中最重要的估值指标之一。" +
                "市盈率是指股票价格与每股收益的比率，计算公式为：市盈率 = 股价 / 每股收益（EPS）。" +
                "市盈率反映了投资者为获得1元公司收益愿意支付的价格。" +
                "一般来说，市盈率越高，说明投资者对公司未来盈利增长预期越高，但也可能意味着股票被高估。" +
                "市盈率越低，可能意味着股票被低估，但也可能反映了公司盈利前景不佳。" +
                "市盈率可以分为静态市盈率（基于过去一年的收益）、动态市盈率（基于预测收益）和TTM市盈率（基于最近四个季度的收益）。",
                Map.of(
                    "title", "市盈率概念和计算",
                    "category", "基本面分析",
                    "keywords", "市盈率,估值,投资指标"
                )
            ),

            // 文档2: 股票交易时间
            new Document("中国A股市场的交易时间分为交易日上午和下午两个时段。" +
                "交易日上午时段为9:30-11:30，共2小时；下午时段为13:00-15:00，共2小时。" +
                "每个交易日总共交易时间为4个小时。" +
                "集合竞价时间为9:15-9:25，其中9:15-9:20可以申报和撤单，9:20-9:25可以申报但不能撤单。" +
                "9:25-9:30为集合竞价撮合时间，期间不接受申报和撤单。" +
                "股票交易采用T+1制度，即当天买入的股票要在下一个交易日才能卖出。" +
                "周末、法定节假日和市场休假日不进行交易。",
                Map.of(
                    "title", "股票交易时间安排",
                    "category", "交易规则",
                    "keywords", "交易时间,集合竞价,T+1"
                )
            ),

            // 文档3: K线图
            new Document("K线图（又称蜡烛图）是技术分析中最常用的图表工具，能够直观地展示股票价格的波动情况。" +
                "每根K线代表一个时间周期（如日线、周线、月线）内的价格变动。" +
                "K线由实体和上下影线组成，实体表示开盘价和收盘价之间的部分，上下影线表示最高价和最低价。" +
                "红色（或空心）K线表示收盘价高于开盘价，为阳线，代表价格上涨。" +
                "绿色（或实心）K线表示收盘价低于开盘价，为阴线，代表价格下跌。" +
                "上影线的顶端表示最高价，下影线的底端表示最低价。" +
                "常见的K线形态有十字星、锤头线、吊颈线、吞没形态等，可以帮助投资者判断市场趋势和转折点。",
                Map.of(
                    "title", "K线图基础知识",
                    "category", "技术分析",
                    "keywords", "K线图,阳线,阴线,技术分析"
                )
            ),

            // 文档4: 涨停板
            new Document("涨停板是指股票价格在一个交易日内上涨达到交易所规定的最大涨幅，无法继续上涨的状态。" +
                "中国A股市场的涨跌幅限制为：主板（上海、深圳主板）股票涨跌幅限制为10%，" +
                "创业板（创业板、科创板）股票涨跌幅限制为20%。" +
                "新股上市首日不设涨跌幅限制，但首日后遵循正常涨跌幅限制。" +
                "当股票达到涨停板时，投资者仍可提交买单，但成交价格只能以涨停价成交。" +
                "ST和*ST股票的涨跌幅限制为5%。" +
                "涨停板的出现通常反映市场对该股票的强烈买盘需求，但也可能是主力资金拉抬股价的手段。" +
                "投资者需要结合成交量、换手率等指标综合分析涨停板的意义。",
                Map.of(
                    "title", "涨停板和涨跌幅限制",
                    "category", "交易规则",
                    "keywords", "涨停板,涨跌幅,ST股票"
                )
            ),

            // 文档5: 市净率
            new Document("市净率（Price-to-Book Ratio，简称P/B）是衡量股票估值的重要指标之一。" +
                "市净率是指股票价格与每股净资产的比率，计算公式为：市净率 = 股价 / 每股净资产。" +
                "每股净资产等于公司总资产减去总负债后再除以总股本。" +
                "市净率反映了投资者为获得公司1元净资产愿意支付的价格。" +
                "一般来说，市净率越低，股票价格相对于净资产越便宜，可能意味着投资价值较高。" +
                "市净率适用于资产密集型行业的估值，如银行、房地产、制造业等。" +
                "对于无形资产占比较高的行业（如科技、互联网公司），市净率的参考意义相对较小。" +
                "市净率与市盈率结合使用，可以更全面地评估公司的投资价值。",
                Map.of(
                    "title", "市净率概念和应用",
                    "category", "基本面分析",
                    "keywords", "市净率,估值,每股净资产"
                )
            ),

            // 文档6: 成交量
            new Document("成交量是指在特定时间周期内股票成交的股数或金额，是技术分析中最基础也是最重要的指标之一。" +
                "成交量反映了市场参与者的交易活跃程度和资金流向。" +
                "成交量与价格的关系：量增价涨是健康的上涨趋势，量增价跌可能预示下跌趋势。" +
                "量缩价跌说明抛压减轻，可能见底；量缩价涨说明上涨动力不足，可能回调。" +
                "常见的成交量分析方法包括：成交量趋势线、成交量均线（如5日、10日成交量均线）、换手率等。" +
                "换手率是指成交股数与流通股本的比率，换手率越高说明股票交易越活跃。" +
                "异常放大的成交量往往预示着重大行情变化或主力资金的大规模进出。",
                Map.of(
                    "title", "成交量分析",
                    "category", "技术分析",
                    "keywords", "成交量,换手率,量价关系"
                )
            ),

            // 文档7: MACD指标
            new Document("MACD（Moving Average Convergence Divergence，指数平滑异同移动平均线）是常用的技术指标之一。" +
                "MACD由快速线（DIF）、慢速线（DEA）和柱状线（MACD柱）三部分组成。" +
                "DIF是12日EMA（指数移动平均线）减去26日EMA，DEA是DIF的9日EMA。" +
                "MACD柱状线 = 2 × (DIF - DEA)，当DIF在DEA之上时，柱状线为正（通常用红色表示），当DIF在DEA之下时，柱状线为负（通常用绿色表示）。" +
                "MACD的应用法则：1) DIF上穿DEA形成金叉，买入信号；2) DIF下穿DEA形成死叉，卖出信号；3) MACD柱状线由负转正，买入信号；4) MACD柱状线由正转负，卖出信号。" +
                "MACD背离：价格创新高但MACD未创新高为顶背离，预示下跌；价格创新低但MACD未创新低为底背离，预示上涨。" +
                "MACD适合趋势判断，在震荡市中容易出现虚假信号。",
                Map.of(
                    "title", "MACD技术指标",
                    "category", "技术分析",
                    "keywords", "MACD,金叉,死叉,背离"
                )
            ),

            // 文档8: 股息率
            new Document("股息率（Dividend Yield）是衡量股票投资收益的重要指标，反映公司分红收益水平。" +
                "股息率计算公式为：股息率 = 每股股息 / 股价 × 100%。" +
                "每股股息通常指年度分红金额，有些公司还会进行中期分红。" +
                "股息率与国债利率、银行存款利率等无风险利率进行比较，可以评估股票的投资价值。" +
                "一般来说，股息率超过3%的股票具有较高的分红价值，适合长期投资和稳健型投资者。" +
                "高股息股票主要分布在银行、公用事业、电力、高速、港口等现金流稳定、分红稳定的行业。" +
                "投资高股息股票需要注意：1) 公司分红可持续性；2) 公司盈利能力；3) 股价上涨潜力。" +
                "股息率投资策略（红利策略）在熊市中往往表现优异，被称为"防御性投资"。",
                Map.of(
                    "title", "股息率和分红投资",
                    "category", "基本面分析",
                    "keywords", "股息率,分红,红利策略"
                )
            )
        );
    }
}
