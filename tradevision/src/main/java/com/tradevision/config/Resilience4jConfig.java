package com.tradevision.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j 설정
 * Circuit Breaker 및 Retry 패턴 설정
 * 외부 API 호출 시 장애 대응 및 안정성 향상
 */
@Configuration
public class Resilience4jConfig {

    /**
     * 한국투자증권 API용 Circuit Breaker 설정
     * 실패율이 50%를 넘으면 Circuit을 Open하여 추가 호출 차단
     */
    @Bean
    public CircuitBreakerConfig kisCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(10)                    // 최근 10개 호출 기준
                .failureRateThreshold(50)                 // 실패율 50% 초과 시 Circuit Open
                .waitDurationInOpenState(Duration.ofSeconds(60))  // Open 상태 60초 유지
                .permittedNumberOfCallsInHalfOpenState(3) // Half-Open 상태에서 3개 호출 허용
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
    }

    /**
     * OpenAI API용 Circuit Breaker 설정
     */
    @Bean
    public CircuitBreakerConfig openaiCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))  // OpenAI는 30초 대기
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
    }

    /**
     * 한국투자증권 API용 Retry 설정
     * 실패 시 최대 3회 재시도, Exponential Backoff 적용
     */
    @Bean
    public RetryConfig kisRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)                           // 최대 3회 시도
                .waitDuration(Duration.ofSeconds(1))      // 기본 대기 시간 1초
                .build();
    }

    /**
     * OpenAI API용 Retry 설정
     * 실패 시 최대 2회 재시도
     */
    @Bean
    public RetryConfig openaiRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(2)                           // 최대 2회 시도
                .waitDuration(Duration.ofSeconds(2))      // 기본 대기 시간 2초
                .build();
    }
}
