package com.tradevision.service;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.dto.request.LoginRequest;
import com.tradevision.dto.request.RefreshTokenRequest;
import com.tradevision.dto.request.SignupRequest;
import com.tradevision.dto.response.AuthResponse;
import com.tradevision.entity.RefreshToken;
import com.tradevision.entity.User;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.UnauthorizedException;
import com.tradevision.repository.RefreshTokenRepository;
import com.tradevision.repository.UserRepository;
import com.tradevision.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 * 회원가입, 로그인, 토큰 갱신 비즈니스 로직 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

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

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
    }

    // ========== 회원가입 테스트 ==========

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword123");
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        Long userId = authService.signup(signupRequest);

        // then
        assertThat(userId).isEqualTo(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
        assertThat(savedUser.getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_DuplicateEmail_ThrowsException() {
        // given
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - InvestmentLevel이 null이면 BEGINNER로 설정")
    void signup_NullInvestmentLevel_DefaultsToBeginner() {
        // given
        SignupRequest requestWithoutLevel = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("테스터")
                .investmentLevel(null)
                .build();

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword123");
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        authService.signup(requestWithoutLevel);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getInvestmentLevel()).isEqualTo(InvestmentLevel.BEGINNER);
    }

    // ========== 로그인 테스트 ==========

    @Test
    @DisplayName("로그인 성공 - Access Token과 Refresh Token 발급")
    void login_Success() {
        // given
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("password123", "encodedPassword123")).willReturn(true);
        given(jwtTokenProvider.generateAccessToken("test@example.com")).willReturn("access-token-12345");
        given(jwtTokenProvider.generateRefreshToken("test@example.com")).willReturn("refresh-token-12345");
        given(jwtTokenProvider.getRefreshTokenValidity()).willReturn(604800000L);

        // when
        AuthResponse authResponse = authService.login(loginRequest);

        // then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getAccessToken()).isEqualTo("access-token-12345");
        assertThat(authResponse.getRefreshToken()).isEqualTo("refresh-token-12345");
        assertThat(authResponse.getUser().getEmail()).isEqualTo("test@example.com");

        verify(refreshTokenRepository).deleteByUserId(1L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_UserNotFound_ThrowsException() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_InvalidPassword_ThrowsException() {
        // given
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("password123", "encodedPassword123")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

        verify(jwtTokenProvider, never()).generateAccessToken(anyString());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 시 기존 Refresh Token 삭제 후 새로 저장")
    void login_DeletesOldRefreshToken_SavesNew() {
        // given
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("password123", "encodedPassword123")).willReturn(true);
        given(jwtTokenProvider.generateAccessToken("test@example.com")).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken("test@example.com")).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenValidity()).willReturn(604800000L);

        // when
        authService.login(loginRequest);

        // then
        verify(refreshTokenRepository).deleteByUserId(1L);

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());

        RefreshToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getToken()).isEqualTo("refresh-token");
        assertThat(savedToken.getUser()).isEqualTo(testUser);
    }

    // ========== 토큰 갱신 테스트 ==========

    @Test
    @DisplayName("토큰 갱신 성공 - 새 Access Token 발급")
    void refresh_Success() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        given(jwtTokenProvider.validateToken("valid-refresh-token")).willReturn(true);
        given(refreshTokenRepository.findByToken("valid-refresh-token")).willReturn(Optional.of(refreshToken));
        given(jwtTokenProvider.generateAccessToken("test@example.com")).willReturn("new-access-token");

        // when
        AuthResponse authResponse = authService.refresh(request);

        // then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getAccessToken()).isEqualTo("new-access-token");
        assertThat(authResponse.getRefreshToken()).isEqualTo("valid-refresh-token");
        assertThat(authResponse.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 Refresh Token")
    void refresh_InvalidToken_ThrowsException() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

        given(jwtTokenProvider.validateToken("invalid-refresh-token")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);

        verify(refreshTokenRepository, never()).findByToken(anyString());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - DB에 존재하지 않는 Refresh Token")
    void refresh_TokenNotFoundInDB_ThrowsException() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        given(jwtTokenProvider.validateToken("valid-refresh-token")).willReturn(true);
        given(refreshTokenRepository.findByToken("valid-refresh-token")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 만료된 Refresh Token")
    void refresh_ExpiredToken_ThrowsException() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest("expired-refresh-token");

        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .token("expired-refresh-token")
                .expiresAt(LocalDateTime.now().minusDays(1)) // 만료됨
                .build();

        given(jwtTokenProvider.validateToken("expired-refresh-token")).willReturn(true);
        given(refreshTokenRepository.findByToken("expired-refresh-token")).willReturn(Optional.of(expiredToken));

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_TOKEN);
    }
}
