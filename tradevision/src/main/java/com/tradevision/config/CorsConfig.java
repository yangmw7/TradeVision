package com.tradevision.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS 설정
 * 프론트엔드 (React) 도메인에서의 API 호출 허용
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * CORS 필터 설정
     * 프론트엔드에서 백엔드 API 호출 시 CORS 에러 방지
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 자격 증명 허용 (쿠키, 인증 헤더 등)
        config.setAllowCredentials(allowCredentials);

        // 허용할 Origin (프론트엔드 도메인)
        List<String> origins = Arrays.asList(allowedOrigins);
        config.setAllowedOriginPatterns(origins);

        // 허용할 HTTP 메서드
        config.setAllowedMethods(Arrays.asList(allowedMethods));

        // 허용할 헤더
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            config.addAllowedHeader("*");
        } else {
            config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        }

        // Preflight 요청 캐시 시간
        config.setMaxAge(maxAge);

        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
