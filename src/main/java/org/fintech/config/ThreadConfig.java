package org.fintech.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.shaded.org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ThreadConfig {

    private final ExecutorService baseThreadPool;
    private final ScheduledExecutorService baseScheduleThreadPool;

    public ThreadConfig(@Value("${data-init.threads-count}") int threadCount,
                        @Value("${data-init.schedule-threads-count}") int scheduleThreadCount) {
        ThreadFactory baseThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("kudago-thread-%d")
                .build();
        this.baseThreadPool = Executors.newFixedThreadPool(threadCount, baseThreadFactory);

        ThreadFactory scheduleThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("kudago-schedule-thread-%d")
                .build();
        this.baseScheduleThreadPool = Executors.newScheduledThreadPool(scheduleThreadCount, scheduleThreadFactory);
    }

    @Bean
    public ExecutorService baseThreadPool() {
        return baseThreadPool;
    }

    @Bean
    public ScheduledExecutorService baseScheduleThreadPool() {
        return baseScheduleThreadPool;
    }

    @PreDestroy
    public void shutdown() {
        baseThreadPool.shutdown();
        baseScheduleThreadPool.shutdown();
    }
}