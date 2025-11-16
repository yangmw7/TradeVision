package com.tradevision.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.dto.response.ApiResponse;
import com.tradevision.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 핸들러 (401 Unauthorized)
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출되는 메서드
     * 401 Unauthorized 응답과 함께 한국어 에러 메시지 반환
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.warn("인증되지 않은 사용자의 접근 시도: {}", request.getRequestURI());

        // 401 Unauthorized 응답 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse 형식으로 에러 응답 생성
        ApiResponse<Void> errorResponse = ApiResponse.error(
                ErrorCode.UNAUTHORIZED.getCode(),
                "인증이 필요합니다. 로그인 후 다시 시도해주세요"
        );

        // JSON으로 변환하여 응답
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
