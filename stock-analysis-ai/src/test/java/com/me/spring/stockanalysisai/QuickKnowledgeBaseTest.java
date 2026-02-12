package com.me.spring.stockanalysisai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 快速测试类 - 验证知识库是否正常工作
 * 使用方法：直接运行此测试类
 */
@SpringBootTest
public class QuickKnowledgeBaseTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    /**
     * 测试1: 验证知识库文档已加载
     */
    @Test
    public void testKnowledgeBaseLoaded() {
        System.out.println("\n========================================");
        System.out.println("测试1: 验证知识库文档加载");
        System.out.println("========================================\n");

        // 执行一个通用查询来获取所有文档
        SearchRequest request = SearchRequest.query("股票").withTopK(20);
        List<org.springframework.ai.document.Document> results = vectorStore.similaritySearch(request);

        System.out.println("✓ 知识库文档数量: " + results.size());

        if (!results.isEmpty()) {
            System.out.println("\n已加载的文档列表:");
            for (int i = 0; i < results.size(); i++) {
                var doc = results.get(i);
                String title = (String) doc.getMetadata().get("title");
                String category = (String) doc.getMetadata().get("category");
                System.out.println("  " + (i + 1) + ". " + title + " [" + category + "]");
            }
        }

        System.out.println("\n========================================\n");
    }

    /**
     * 测试2: 向量检索功能
     */
    @Test
    public void testVectorSearch() {
        System.out.println("\n========================================");
        System.out.println("测试2: 向量检索功能");
        System.out.println("========================================\n");

        String query = "什么是市盈率？";
        System.out.println("查询: " + query + "\n");

        SearchRequest request = SearchRequest.query(query).withTopK(3);
        var results = vectorStore.similaritySearch(request);

        System.out.println("检索到 " + results.size() + " 条相关文档:\n");
        for (int i = 0; i < results.size(); i++) {
            var doc = results.get(i);
            String title = (String) doc.getMetadata().get("title");
            String content = doc.getContent().substring(0, Math.min(100, doc.getContent().length()));
            Double distance = doc.getMetadata().get("distance") != null 
                ? (Double) doc.getMetadata().get("distance") 
                : 0.0;
            Double similarity = 1.0 - distance;

            System.out.println((i + 1) + ". " + title);
            System.out.println("   相似度: " + String.format("%.4f", similarity));
            System.out.println("   内容: " + content + "...\n");
        }

        System.out.println("========================================\n");
    }

    /**
     * 测试3: RAG问答测试 - 市盈率
     */
    @Test
    public void testRAGQuestion_PE_Ratio() {
        String question = "请解释什么是市盈率，它的计算公式是什么，有什么作用？";
        testRAGAnswer("市盈率", question);
    }

    /**
     * 测试4: RAG问答测试 - 交易时间
     */
    @Test
    public void testRAGQuestion_Trading_Time() {
        String question = "中国A股市场的交易时间是怎样的？包括集合竞价时间。";
        testRAGAnswer("交易时间", question);
    }

    /**
     * 测试5: RAG问答测试 - K线图
     */
    @Test
    public void testRAGQuestion_KLine() {
        String question = "K线图中的红色和绿色分别代表什么意思？";
        testRAGAnswer("K线图", question);
    }

    /**
     * 测试6: RAG问答测试 - 涨停板
     */
    @Test
    public void testRAGQuestion_Limit_Up() {
        String question = "什么是涨停板？不同板块的涨跌幅限制分别是多少？";
        testRAGAnswer("涨停板", question);
    }

    /**
     * 测试7: RAG问答测试 - 市净率
     */
    @Test
    public void testRAGQuestion_PB_Ratio() {
        String question = "市净率是什么？它适用于哪些行业？";
        testRAGAnswer("市净率", question);
    }

    /**
     * 测试8: RAG问答测试 - 成交量
     */
    @Test
    public void testRAGQuestion_Volume() {
        String question = "成交量和价格之间有什么关系？量增价涨意味着什么？";
        testRAGAnswer("成交量", question);
    }

    /**
     * 测试9: RAG问答测试 - MACD
     */
    @Test
    public void testRAGQuestion_MACD() {
        String question = "MACD指标的金叉和死叉分别代表什么信号？";
        testRAGAnswer("MACD", question);
    }

    /**
     * 测试10: RAG问答测试 - 股息率
     */
    @Test
    public void testRAGQuestion_Dividend() {
        String question = "什么是股息率？高股息股票有什么特点？";
        testRAGAnswer("股息率", question);
    }

    /**
     * 测试11: RAG问答测试 - ETF
     */
    @Test
    public void testRAGQuestion_ETF() {
        String question = "什么是ETF基金？它有什么优点？";
        testRAGAnswer("ETF", question);
    }

    /**
     * 测试12: RAG问答测试 - 量化交易
     */
    @Test
    public void testRAGQuestion_Quantitative() {
        String question = "什么是量化交易？它有什么特点？";
        testRAGAnswer("量化交易", question);
    }

    /**
     * 测试13: 知识库外问题
     */
    @Test
    public void testOutOfScopeQuestion() {
        String question = "今天北京的天气怎么样？";
        testRAGAnswer("知识库外问题", question);
    }

    /**
     * 通用RAG测试方法
     */
    private void testRAGAnswer(String testName, String question) {
        System.out.println("\n========================================");
        System.out.println("测试: " + testName);
        System.out.println("========================================\n");
        System.out.println("问题: " + question + "\n");

        long startTime = System.currentTimeMillis();

        try {
            String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            System.out.println("回答: " + answer);
            System.out.println("\n⏱ 响应时间: " + responseTime + "ms");
        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================\n");
    }

    /**
     * 综合测试 - 运行所有测试
     */
    @Test
    public void testAllQuestions() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      综合测试 - RAG知识库问答           ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        testKnowledgeBaseLoaded();
        
        String[] questions = {
            "什么是市盈率？",
            "股票交易时间是什么时候？",
            "K线图是什么意思？",
            "涨停板有什么限制？",
            "什么是市净率？"
        };

        for (int i = 0; i < questions.length; i++) {
            System.out.println("\n【问题 " + (i + 1) + "】");
            testRAGAnswer("综合测试", questions[i]);
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         所有测试完成！                   ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
}
