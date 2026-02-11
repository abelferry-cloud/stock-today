package com.me.stock.pojo.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "task.pool")
@Data
public class TaskThreadPoolInfo {
    /**
     *  核心线程数（获取硬件）：线程池创建时候初始化的线程数
     */
    private Integer corePoolSize;

    /**
     *  最大线程数（获取硬件）：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
     */
    private Integer maxPoolSize;

    /**
     *  线程池的缓冲队列：用来缓冲执行任务的队列
     */
    private Integer keepAliveSeconds;

    /**
     *  缓冲队列大小：用来缓冲执行任务的队列大小
     */
    private Integer queueCapacity;
}