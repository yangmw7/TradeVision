package com.tradevision.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * OpenAI Vision API 호출 등 시간이 오래 걸리는 작업을 비동기로 처리
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 비동기 작업을 위한 ThreadPool 설정
     * OpenAI API 호출 등에 사용
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // 기본 스레드 수
        executor.setMaxPoolSize(10);           // 최대 스레드 수
        executor.setQueueCapacity(100);        // 큐 용량
        executor.setThreadNamePrefix("async-"); // 스레드 이름 접두사
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
