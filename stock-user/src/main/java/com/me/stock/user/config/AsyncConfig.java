package com.me.stock.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 * 配置异步任务执行的线程池
 *
 * @author stock-user
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列大小
        executor.setQueueCapacity(100);
        // 线程名称前缀
        executor.setThreadNamePrefix("async-executor-");
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 等待任务全部结束再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 关闭时等待的时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }
}
