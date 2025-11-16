package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.entity.RefreshToken;
import com.tradevision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RefreshTokenRepository 단위 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("RefreshTokenRepository 테스트")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        testUser = userRepository.save(testUser);

        // 테스트용 Refresh Token 생성
        testRefreshToken = RefreshToken.builder()
                .user(testUser)
                .token("test-refresh-token-12345")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Refresh Token 저장 성공")
    void saveRefreshToken_Success() {
        // when
        RefreshToken savedToken = refreshTokenRepository.save(testRefreshToken);

        // then
        assertThat(savedToken.getId()).isNotNull();
        assertThat(savedToken.getToken()).isEqualTo("test-refresh-token-12345");
        assertThat(savedToken.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedToken.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("토큰 문자열로 Refresh Token 조회 성공")
    void findByToken_Success() {
        // given
        refreshTokenRepository.save(testRefreshToken);

        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("test-refresh-token-12345");

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("test-refresh-token-12345");
        assertThat(foundToken.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 빈 Optional 반환")
    void findByToken_NotFound() {
        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("non-existent-token");

        // then
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 Refresh Token 삭제 성공")
    void deleteByUserId_Success() {
        // given
        refreshTokenRepository.save(testRefreshToken);

        // when
        refreshTokenRepository.deleteByUserId(testUser.getId());

        // then
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("test-refresh-token-12345");
        assertThat(foundToken).isEmpty();
    }

    @Test
    @DisplayName("Refresh Token 만료 확인 - 만료되지 않음")
    void isExpired_NotExpired() {
        // given
        RefreshToken validToken = RefreshToken.builder()
                .user(testUser)
                .token("valid-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        // when
        boolean isExpired = validToken.isExpired();

        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Refresh Token 만료 확인 - 만료됨")
    void isExpired_Expired() {
        // given
        RefreshToken expiredToken = RefreshToken.builder()
                .user(testUser)
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        // when
        boolean isExpired = expiredToken.isExpired();

        // then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("사용자 삭제 시 연관된 Refresh Token도 삭제 (CASCADE)")
    void cascadeDelete_OnUserDelete() {
        // given
        refreshTokenRepository.save(testRefreshToken);
        Long userId = testUser.getId();

        // when
        userRepository.deleteById(userId);

        // then
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("test-refresh-token-12345");
        assertThat(foundToken).isEmpty();
    }
}
