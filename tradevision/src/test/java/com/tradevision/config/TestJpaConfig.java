package com.tradevision.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * 테스트용 JPA 설정
 * JPA Auditing 활성화
 */
@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {

    /**
     * 테스트용 AuditorAware
     * created_at, updated_at 자동 설정을 위해 필요
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }
}
