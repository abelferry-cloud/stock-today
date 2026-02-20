package com.me.spring.stock.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API Key配置测试类
 * 用于验证环境变量中的API密钥配置是否正确加载
 */
@Slf4j
@SpringBootTest
class ApiKeyConfigTest {

    @Autowired
    private ApiKeyConfig apiKeyConfig;

    @Test
    void testApiKeyConfigLoaded() {
        log.info("=== API Key配置加载测试 ===");
        
        // 验证配置对象不为空
        assertNotNull(apiKeyConfig, "ApiKeyConfig不应为空");
        assertNotNull(apiKeyConfig.getDeepseek(), "DeepSeek配置不应为空");
        assertNotNull(apiKeyConfig.getDashscope(), "DashScope配置不应为空");
        assertNotNull(apiKeyConfig.getPinecone(), "Pinecone配置不应为空");
        
        log.info("配置对象加载成功");
    }

    @Test
    void testDeepSeekApiKey() {
        String apiKey = apiKeyConfig.getDeepseek().getApiKey();
        log.info("DeepSeek API Key: {}", maskApiKey(apiKey));
        
        // 如果环境变量已设置，验证值不为空
        if (apiKey != null && !apiKey.isEmpty()) {
            assertTrue(apiKey.length() > 10, "DeepSeek API Key长度应大于10");
            log.info("DeepSeek API Key配置成功");
        } else {
            log.warn("DeepSeek API Key未配置（环境变量DEEPSEEK_API_KEY未设置）");
        }
    }

    @Test
    void testDashScopeApiKey() {
        String apiKey = apiKeyConfig.getDashscope().getApiKey();
        log.info("DashScope API Key: {}", maskApiKey(apiKey));
        
        if (apiKey != null && !apiKey.isEmpty()) {
            assertTrue(apiKey.length() > 10, "DashScope API Key长度应大于10");
            log.info("DashScope API Key配置成功");
        } else {
            log.warn("DashScope API Key未配置（环境变量DASHSCOPE_API_KEY未设置）");
        }
    }

    @Test
    void testPineconeApiKey() {
        String apiKey = apiKeyConfig.getPinecone().getApiKey();
        log.info("Pinecone API Key: {}", maskApiKey(apiKey));
        
        if (apiKey != null && !apiKey.isEmpty()) {
            assertTrue(apiKey.length() > 10, "Pinecone API Key长度应大于10");
            log.info("Pinecone API Key配置成功");
        } else {
            log.warn("Pinecone API Key未配置（环境变量PINECONE_API_KEY未设置）");
        }
    }

    /**
     * 遮蔽API Key，只显示前后几位字符
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "(未设置)";
        }
        if (apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
