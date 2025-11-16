package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 단위 테스트
 * @DataJpaTest: JPA 컴포넌트만 로드하여 빠른 테스트 수행
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 데이터 준비
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
    }

    @Test
    @DisplayName("사용자 저장 성공")
    void saveUser_Success() {
        // when
        User savedUser = userRepository.save(testUser);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("테스터");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findByEmail_Success() {
        // given
        userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional 반환")
    void findByEmail_NotFound() {
        // when
        Optional<User> foundUser = userRepository.findByEmail("notexist@example.com");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하는 경우")
    void existsByEmail_True() {
        // given
        userRepository.save(testUser);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하지 않는 경우")
    void existsByEmail_False() {
        // when
        boolean exists = userRepository.existsByEmail("notexist@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser_Success() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        savedUser.updateProfile("새닉네임", InvestmentLevel.INTERMEDIATE);
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getNickname()).isEqualTo("새닉네임");
        assertThat(updatedUser.getInvestmentLevel()).isEqualTo(InvestmentLevel.INTERMEDIATE);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() {
        // given
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        // when
        userRepository.deleteById(userId);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
