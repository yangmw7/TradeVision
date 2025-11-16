package com.tradevision.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 단위 테스트
 * JWT 토큰 생성, 검증, 파싱 기능 테스트
 */
@DisplayName("JwtTokenProvider 테스트")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String testSecretKey;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        // 테스트용 시크릿 키 (256비트 이상)
        testSecretKey = "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long";
        secretKey = Keys.hmacShaKeyFor(testSecretKey.getBytes(StandardCharsets.UTF_8));

        // JwtTokenProvider 생성 (생성자 인자: secret, accessTokenValidity, refreshTokenValidity)
        jwtTokenProvider = new JwtTokenProvider(
                testSecretKey,
                3600000L,  // 1시간
                604800000L // 7일
        );
    }

    @Test
    @DisplayName("Access Token 생성 성공")
    void generateAccessToken_Success() {
        // given
        String email = "test@example.com";

        // when
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
    }

    @Test
    @DisplayName("Refresh Token 생성 성공")
    void generateRefreshToken_Success() {
        // given
        String email = "test@example.com";

        // when
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_ValidToken_ReturnsTrue() {
        // given
        String email = "test@example.com";
        String token = jwtTokenProvider.generateAccessToken(email);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 실패")
    void validateToken_MalformedToken_ReturnsFalse() {
        // given
        String malformedToken = "invalid.token.format";

        // when
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateToken_ExpiredToken_ReturnsFalse() {
        // given
        String email = "test@example.com";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // 1초 전 만료

        String expiredToken = Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 검증 실패")
    void validateToken_InvalidSignature_ReturnsFalse() {
        // given
        String email = "test@example.com";
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "wrong-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String tokenWithWrongSignature = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(wrongKey)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(tokenWithWrongSignature);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰에서 이메일 추출 성공")
    void getEmailFromToken_Success() {
        // given
        String email = "test@example.com";
        String token = jwtTokenProvider.generateAccessToken(email);

        // when
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Refresh Token Validity 조회 성공")
    void getRefreshTokenValidity_Success() {
        // when
        long validity = jwtTokenProvider.getRefreshTokenValidity();

        // then
        assertThat(validity).isEqualTo(604800000L); // 7일
    }

    @Test
    @DisplayName("빈 토큰 검증 실패")
    void validateToken_EmptyToken_ReturnsFalse() {
        // given
        String emptyToken = "";

        // when
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("null 토큰 검증 실패")
    void validateToken_NullToken_ReturnsFalse() {
        // when
        boolean isValid = jwtTokenProvider.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }
}
