package com.tradevision.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.dto.request.LoginRequest;
import com.tradevision.dto.request.RefreshTokenRequest;
import com.tradevision.dto.request.SignupRequest;
import com.tradevision.dto.response.AuthResponse;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.UnauthorizedException;
import com.tradevision.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 단위 테스트
 * MockMvc를 사용한 컨트롤러 레이어 테스트
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("access-token-12345")
                .refreshToken("refresh-token-12345")
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(1L)
                        .email("test@example.com")
                        .nickname("테스터")
                        .investmentLevel(InvestmentLevel.BEGINNER)
                        .build())
                .build();
    }

    // ========== 회원가입 테스트 ==========

    @Test
    @DisplayName("POST /api/auth/signup - 회원가입 성공")
    @WithMockUser
    void signup_Success() throws Exception {
        // given
        given(authService.signup(any(SignupRequest.class))).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @DisplayName("POST /api/auth/signup - 유효성 검증 실패 (이메일 형식 오류)")
    @WithMockUser
    void signup_InvalidEmail_ValidationFails() throws Exception {
        // given
        SignupRequest invalidRequest = SignupRequest.builder()
                .email("invalid-email")  // 잘못된 이메일 형식
                .password("password123")
                .nickname("테스터")
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/signup - 유효성 검증 실패 (비밀번호 길이 부족)")
    @WithMockUser
    void signup_ShortPassword_ValidationFails() throws Exception {
        // given
        SignupRequest invalidRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("short")  // 8자 미만
                .nickname("테스터")
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/signup - 이메일 중복 에러")
    @WithMockUser
    void signup_DuplicateEmail_ThrowsException() throws Exception {
        // given
        given(authService.signup(any(SignupRequest.class)))
                .willThrow(new BusinessException(ErrorCode.DUPLICATE_EMAIL));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1002"));
    }

    // ========== 로그인 테스트 ==========

    @Test
    @DisplayName("POST /api/auth/login - 로그인 성공")
    @WithMockUser
    void login_Success() throws Exception {
        // given
        given(authService.login(any(LoginRequest.class))).willReturn(authResponse);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-12345"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-12345"))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 유효성 검증 실패 (이메일 누락)")
    @WithMockUser
    void login_MissingEmail_ValidationFails() throws Exception {
        // given
        LoginRequest invalidRequest = LoginRequest.builder()
                .email(null)  // 이메일 누락
                .password("password123")
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - 잘못된 인증 정보")
    @WithMockUser
    void login_InvalidCredentials_ThrowsException() throws Exception {
        // given
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1001"));
    }

    // ========== 토큰 갱신 테스트 ==========

    @Test
    @DisplayName("POST /api/auth/refresh - 토큰 갱신 성공")
    @WithMockUser
    void refresh_Success() throws Exception {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        AuthResponse refreshResponse = AuthResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("valid-refresh-token")
                .tokenType("Bearer")
                .user(authResponse.getUser())
                .build();

        given(authService.refresh(any(RefreshTokenRequest.class))).willReturn(refreshResponse);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("토큰이 갱신되었습니다"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("valid-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - 유효하지 않은 Refresh Token")
    @WithMockUser
    void refresh_InvalidToken_ThrowsException() throws Exception {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

        given(authService.refresh(any(RefreshTokenRequest.class)))
                .willThrow(new UnauthorizedException(ErrorCode.INVALID_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1003"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - 만료된 Refresh Token")
    @WithMockUser
    void refresh_ExpiredToken_ThrowsException() throws Exception {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("expired-refresh-token");

        given(authService.refresh(any(RefreshTokenRequest.class)))
                .willThrow(new UnauthorizedException(ErrorCode.EXPIRED_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1004"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - 유효성 검증 실패 (Refresh Token 누락)")
    @WithMockUser
    void refresh_MissingToken_ValidationFails() throws Exception {
        // given
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest(null);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
