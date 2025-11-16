package com.tradevision.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * JwtAuthenticationFilter 단위 테스트
 * JWT 토큰 추출 및 인증 처리 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공")
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // given
        String token = "valid-jwt-token";
        String email = "test@example.com";

        UserDetails userDetails = User.builder()
                .username(email)
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();

        given(request.getHeader("Authorization")).willReturn("Bearer " + token);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.getEmailFromToken(token)).willReturn(email);
        given(userDetailsService.loadUserByUsername(email)).willReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.isAuthenticated()).isTrue();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더 없음 - 인증 설정 안 함")
    void doFilterInternal_NoAuthorizationHeader_NoAuthentication() throws ServletException, IOException {
        // given
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 접두사 없는 토큰 - 인증 설정 안 함")
    void doFilterInternal_NoBearerPrefix_NoAuthentication() throws ServletException, IOException {
        // given
        given(request.getHeader("Authorization")).willReturn("InvalidToken");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰 - 인증 설정 안 함")
    void doFilterInternal_InvalidToken_NoAuthentication() throws ServletException, IOException {
        // given
        String token = "invalid-jwt-token";

        given(request.getHeader("Authorization")).willReturn("Bearer " + token);
        given(tokenProvider.validateToken(token)).willReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰 처리 중 예외 발생 - 필터 체인 계속 진행")
    void doFilterInternal_ExceptionThrown_ContinuesFilterChain() throws ServletException, IOException {
        // given
        String token = "valid-jwt-token";

        given(request.getHeader("Authorization")).willReturn("Bearer " + token);
        given(tokenProvider.validateToken(token)).willReturn(true);
        given(tokenProvider.getEmailFromToken(token)).willThrow(new RuntimeException("토큰 파싱 실패"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }
}
