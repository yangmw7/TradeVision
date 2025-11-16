package com.tradevision.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.dto.request.LoginRequest;
import com.tradevision.dto.request.RefreshTokenRequest;
import com.tradevision.dto.request.SignupRequest;
import com.tradevision.entity.User;
import com.tradevision.repository.RefreshTokenRepository;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 인증 통합 테스트
 * 실제 Spring Context를 로드하여 전체 인증 플로우 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("인증 통합 테스트")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 초기화
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 → 로그인 → 토큰 갱신 전체 플로우 테스트")
    void fullAuthenticationFlow_Success() throws Exception {
        // ========== 1. 회원가입 ==========
        SignupRequest signupRequest = SignupRequest.builder()
                .email("integration@test.com")
                .password("password123")
                .nickname("통합테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNumber());

        // 사용자가 DB에 저장되었는지 확인
        User savedUser = userRepository.findByEmail("integration@test.com").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.getNickname()).isEqualTo("통합테스터");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();

        // ========== 2. 로그인 ==========
        LoginRequest loginRequest = LoginRequest.builder()
                .email("integration@test.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("integration@test.com"))
                .andReturn();

        // 응답에서 Refresh Token 추출
        String responseBody = loginResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(responseBody)
                .path("data")
                .path("refreshToken")
                .asText();

        // Refresh Token이 DB에 저장되었는지 확인
        assertThat(refreshTokenRepository.findByToken(refreshToken)).isPresent();

        // ========== 3. 토큰 갱신 ==========
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken))
                .andExpect(jsonPath("$.data.user.email").value("integration@test.com"));
    }

    @Test
    @DisplayName("중복 이메일 회원가입 실패 테스트")
    void signup_DuplicateEmail_Fails() throws Exception {
        // given: 이미 가입된 사용자
        User existingUser = User.builder()
                .email("duplicate@test.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("기존사용자")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        userRepository.save(existingUser);

        // when: 동일한 이메일로 회원가입 시도
        SignupRequest signupRequest = SignupRequest.builder()
                .email("duplicate@test.com")
                .password("newpassword123")
                .nickname("새사용자")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();

        // then: 400 Bad Request 응답
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1002"));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패 테스트")
    void login_InvalidPassword_Fails() throws Exception {
        // given: 가입된 사용자
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("correctPassword"))
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        userRepository.save(user);

        // when: 잘못된 비밀번호로 로그인 시도
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("wrongPassword")
                .build();

        // then: 401 Unauthorized 응답
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1001"));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 로그인 실패 테스트")
    void login_UserNotFound_Fails() throws Exception {
        // when: 존재하지 않는 이메일로 로그인 시도
        LoginRequest loginRequest = LoginRequest.builder()
                .email("notexist@test.com")
                .password("password123")
                .build();

        // then: 401 Unauthorized 응답
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1001"));
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token으로 갱신 실패 테스트")
    void refresh_InvalidToken_Fails() throws Exception {
        // when: 유효하지 않은 Refresh Token으로 갱신 시도
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-token-12345");

        // then: 401 Unauthorized 응답
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("1003"));
    }

    @Test
    @DisplayName("재로그인 시 기존 Refresh Token 삭제 확인")
    void login_DeletesOldRefreshToken() throws Exception {
        // given: 가입된 사용자
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password123")
                .build();

        // when: 첫 번째 로그인
        MvcResult firstLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String firstRefreshToken = objectMapper.readTree(firstLogin.getResponse().getContentAsString())
                .path("data")
                .path("refreshToken")
                .asText();

        // when: 두 번째 로그인
        MvcResult secondLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String secondRefreshToken = objectMapper.readTree(secondLogin.getResponse().getContentAsString())
                .path("data")
                .path("refreshToken")
                .asText();

        // then: 첫 번째 Refresh Token은 DB에서 삭제되고, 두 번째만 존재
        assertThat(refreshTokenRepository.findByToken(firstRefreshToken)).isEmpty();
        assertThat(refreshTokenRepository.findByToken(secondRefreshToken)).isPresent();

        // 사용자당 Refresh Token은 1개만 존재해야 함
        long tokenCount = refreshTokenRepository.count();
        assertThat(tokenCount).isEqualTo(1);
    }
}
