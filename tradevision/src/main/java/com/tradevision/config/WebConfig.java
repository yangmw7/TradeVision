package com.tradevision.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 * Spring MVC 관련 추가 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 추가적인 Web MVC 설정이 필요한 경우 여기에 구현
    // 예: 인터셉터, 포매터, 메시지 컨버터 등

    // 현재는 기본 설정 사용
    // CORS는 CorsConfig에서 별도 관리
}
