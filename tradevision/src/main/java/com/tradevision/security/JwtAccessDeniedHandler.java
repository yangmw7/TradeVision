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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 권한 거부 핸들러 (403 Forbidden)
 * 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);
    private final ObjectMapper objectMapper;

    /**
     * 권한 거부 시 호출되는 메서드
     * 403 Forbidden 응답과 함께 한국어 에러 메시지 반환
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("권한이 없는 사용자의 접근 시도: {}", request.getRequestURI());

        // 403 Forbidden 응답 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse 형식으로 에러 응답 생성
        ApiResponse<Void> errorResponse = ApiResponse.error(
                ErrorCode.FORBIDDEN.getCode(),
                "접근 권한이 없습니다"
        );

        // JSON으로 변환하여 응답
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
