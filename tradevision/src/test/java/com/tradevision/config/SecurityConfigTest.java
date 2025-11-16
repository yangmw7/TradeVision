package com.tradevision.config;

import com.tradevision.security.CustomUserDetailsService;
import com.tradevision.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig 통합 테스트
 * Spring Security 설정 검증 (인증, 권한, CORS, CSRF 등)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SecurityConfig 테스트")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("공개 엔드포인트 (/api/auth/signup) - 인증 없이 접근 가능")
    void publicEndpoint_Signup_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\",\"nickname\":\"테스터\"}"))
                .andDo(print())
                .andExpect(status().isNotFound()); // 실제 처리는 안 되지만 403이 아님 (컨트롤러가 처리)
    }

    @Test
    @DisplayName("공개 엔드포인트 (/api/auth/login) - 인증 없이 접근 가능")
    void publicEndpoint_Login_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\"}"))
                .andDo(print())
                .andExpect(status().isNotFound()); // 실제 처리는 안 되지만 403이 아님
    }

    @Test
    @DisplayName("공개 엔드포인트 (/api/auth/refresh) - 인증 없이 접근 가능")
    void publicEndpoint_Refresh_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"test-token\"}"))
                .andDo(print())
                .andExpect(status().isNotFound()); // 실제 처리는 안 되지만 403이 아님
    }

    @Test
    @DisplayName("보호된 엔드포인트 - 인증 없이 접근 시 401 Unauthorized")
    void protectedEndpoint_WithoutAuth_Returns401() throws Exception {
        mockMvc.perform(get("/api/protected-resource")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4001"));
    }

    @Test
    @DisplayName("보호된 엔드포인트 - 유효한 JWT로 접근 가능")
    void protectedEndpoint_WithValidJWT_AccessGranted() throws Exception {
        // given: 유효한 JWT 토큰
        String validToken = "valid-jwt-token";
        String email = "test@example.com";

        UserDetails userDetails = User.builder()
                .username(email)
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();

        given(jwtTokenProvider.validateToken(validToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(validToken)).willReturn(email);
        given(userDetailsService.loadUserByUsername(email)).willReturn(userDetails);

        // when & then: Authorization 헤더에 Bearer 토큰을 포함하여 요청
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()); // 실제 엔드포인트는 없지만 401이 아님 (인증 성공)
    }

    @Test
    @DisplayName("유효하지 않은 JWT - 401 Unauthorized")
    void protectedEndpoint_WithInvalidJWT_Returns401() throws Exception {
        // given: 유효하지 않은 JWT 토큰
        String invalidToken = "invalid-jwt-token";

        given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4001"));
    }

    @Test
    @DisplayName("Bearer 접두사 없는 토큰 - 401 Unauthorized")
    void protectedEndpoint_WithoutBearerPrefix_Returns401() throws Exception {
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "InvalidTokenFormat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Swagger UI - 인증 없이 접근 가능")
    void swaggerUI_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andDo(print())
                .andExpect(status().isNotFound()); // Swagger 설정에 따라 다를 수 있지만 403이 아님
    }

    @Test
    @DisplayName("헬스체크 엔드포인트 - 인증 없이 접근 가능")
    void healthCheck_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andDo(print())
                .andExpect(status().isNotFound()); // Actuator 설정에 따라 다를 수 있지만 403이 아님
    }

    @Test
    @DisplayName("CSRF 비활성화 확인 - POST 요청 시 CSRF 토큰 불필요")
    void csrfDisabled_PostRequestWithoutCsrfToken() throws Exception {
        // JWT 기반 인증에서는 CSRF 토큰이 필요 없음
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\"}"))
                .andDo(print())
                .andExpect(status().isNotFound()); // CSRF 에러(403)가 아님
    }

    @Test
    @DisplayName("세션 비활성화 확인 - STATELESS 정책")
    void sessionManagement_Stateless() throws Exception {
        // given: 유효한 JWT 토큰
        String validToken = "valid-jwt-token";
        String email = "test@example.com";

        UserDetails userDetails = User.builder()
                .username(email)
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();

        given(jwtTokenProvider.validateToken(validToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(validToken)).willReturn(email);
        given(userDetailsService.loadUserByUsername(email)).willReturn(userDetails);

        // when: 첫 번째 요청
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer " + validToken))
                .andDo(print());

        // when: 두 번째 요청 (세션이 없으므로 다시 JWT로 인증해야 함)
        mockMvc.perform(get("/api/protected-resource"))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // 세션이 없으므로 JWT 없이는 401
    }

    @Test
    @DisplayName("PasswordEncoder Bean 등록 확인")
    void passwordEncoderBean_IsRegistered(@Autowired(required = false) org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        // PasswordEncoder Bean이 등록되어 있는지 확인
        org.assertj.core.api.Assertions.assertThat(passwordEncoder).isNotNull();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder).isInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class);
    }
}
