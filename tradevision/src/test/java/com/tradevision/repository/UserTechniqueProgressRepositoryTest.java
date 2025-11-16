package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.ProgressStatus;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.TradingTechnique;
import com.tradevision.entity.User;
import com.tradevision.entity.UserTechniqueProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserTechniqueProgressRepository 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EnableJpaAuditing
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UserTechniqueProgressRepositoryTest {

    @Autowired
    private UserTechniqueProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TradingTechniqueRepository techniqueRepository;

    private User user;
    private TradingTechnique technique1;
    private TradingTechnique technique2;
    private TradingTechnique technique3;

    @BeforeEach
    void setUp() {
        progressRepository.deleteAll();
        techniqueRepository.deleteAll();
        userRepository.deleteAll();

        // 사용자 생성
        user = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        userRepository.save(user);

        // 기법 생성
        technique1 = TradingTechnique.builder()
                .name("이동평균선")
                .nameEn("MA")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .summary("기초 기법")
                .description("설명")
                .riskLevel(2)
                .build();

        technique2 = TradingTechnique.builder()
                .name("볼린저밴드")
                .nameEn("BB")
                .difficultyLevel(InvestmentLevel.INTERMEDIATE)
                .category(TechniqueCategory.BOLLINGER_BANDS)
                .summary("중급 기법")
                .description("설명")
                .riskLevel(3)
                .build();

        technique3 = TradingTechnique.builder()
                .name("MACD")
                .nameEn("MACD")
                .difficultyLevel(InvestmentLevel.ADVANCED)
                .category(TechniqueCategory.MACD)
                .summary("고급 기법")
                .description("설명")
                .riskLevel(4)
                .build();

        techniqueRepository.save(technique1);
        techniqueRepository.save(technique2);
        techniqueRepository.save(technique3);

        // 진행도 생성
        UserTechniqueProgress progress1 = UserTechniqueProgress.builder()
                .user(user)
                .technique(technique1)
                .status(ProgressStatus.COMPLETED)
                .progressPercentage(100)
                .isBookmarked(true)
                .userRating(5)
                .isCompleted(true)
                .build();

        UserTechniqueProgress progress2 = UserTechniqueProgress.builder()
                .user(user)
                .technique(technique2)
                .status(ProgressStatus.IN_PROGRESS)
                .progressPercentage(50)
                .isBookmarked(false)
                .isCompleted(false)
                .build();

        UserTechniqueProgress progress3 = UserTechniqueProgress.builder()
                .user(user)
                .technique(technique3)
                .status(ProgressStatus.NOT_STARTED)
                .progressPercentage(0)
                .isBookmarked(true)
                .isCompleted(false)
                .build();

        progressRepository.save(progress1);
        progressRepository.save(progress2);
        progressRepository.save(progress3);
    }

    @Test
    @DisplayName("사용자별 진행도 조회")
    void findByUserIdOrderByUpdatedAtDesc() {
        // when
        Page<UserTechniqueProgress> result = progressRepository.findByUserIdOrderByUpdatedAtDesc(
                user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(p -> p.getUser().getId())
                .containsOnly(user.getId());
    }

    @Test
    @DisplayName("사용자의 특정 기법 진행도 조회")
    void findByUserIdAndTechniqueId() {
        // when
        Optional<UserTechniqueProgress> result = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTechnique().getName()).isEqualTo("이동평균선");
        assertThat(result.get().getProgressPercentage()).isEqualTo(100);
    }

    @Test
    @DisplayName("사용자의 상태별 진행도 조회")
    void findByUserIdAndStatusOrderByUpdatedAtDesc() {
        // when
        Page<UserTechniqueProgress> result = progressRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(
                user.getId(), ProgressStatus.IN_PROGRESS, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(ProgressStatus.IN_PROGRESS);
        assertThat(result.getContent().get(0).getTechnique().getName()).isEqualTo("볼린저밴드");
    }

    @Test
    @DisplayName("사용자의 북마크된 기법 조회")
    void findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc() {
        // when
        Page<UserTechniqueProgress> result = progressRepository
                .findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("isBookmarked").containsOnly(true);
    }

    @Test
    @DisplayName("사용자의 완료된 기법 조회")
    void findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc() {
        // when
        Page<UserTechniqueProgress> result = progressRepository
                .findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsCompleted()).isTrue();
        assertThat(result.getContent().get(0).getTechnique().getName()).isEqualTo("이동평균선");
    }

    @Test
    @DisplayName("사용자의 완료 개수 조회")
    void countByUserIdAndIsCompletedTrue() {
        // when
        long count = progressRepository.countByUserIdAndIsCompletedTrue(user.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 진행중인 기법 개수")
    void countByUserIdAndStatus() {
        // when
        long count = progressRepository.countByUserIdAndStatus(user.getId(), ProgressStatus.IN_PROGRESS);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 전체 진행률 계산")
    void calculateAverageProgress() {
        // when
        Double avgProgress = progressRepository.calculateAverageProgress(user.getId());

        // then
        // (100 + 50 + 0) / 3 = 50.0
        assertThat(avgProgress).isNotNull();
        assertThat(avgProgress).isEqualTo(50.0);
    }

    @Test
    @DisplayName("사용자의 평점 기준 기법 조회")
    void findByUserIdAndUserRatingIsNotNullOrderByUserRatingDescUpdatedAtDesc() {
        // when
        Page<UserTechniqueProgress> result = progressRepository
                .findByUserIdAndUserRatingIsNotNullOrderByUserRatingDescUpdatedAtDesc(
                        user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("진행도 업데이트 - 완료 처리")
    void updateProgress_Complete() {
        // given
        UserTechniqueProgress progress = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();

        // when
        progress.updateProgress(ProgressStatus.COMPLETED, 100);
        progressRepository.save(progress);

        // then
        UserTechniqueProgress updated = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
        assertThat(updated.getProgressPercentage()).isEqualTo(100);
        assertThat(updated.getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("북마크 토글")
    void toggleBookmark() {
        // given
        UserTechniqueProgress progress = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique1.getId()).orElseThrow();
        boolean initialBookmark = progress.getIsBookmarked();

        // when
        progress.toggleBookmark();
        progressRepository.save(progress);

        // then
        UserTechniqueProgress updated = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique1.getId()).orElseThrow();
        assertThat(updated.getIsBookmarked()).isEqualTo(!initialBookmark);
    }

    @Test
    @DisplayName("평점 업데이트")
    void updateRating() {
        // given
        UserTechniqueProgress progress = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();

        // when
        progress.updateRating(4);
        progressRepository.save(progress);

        // then
        UserTechniqueProgress updated = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();
        assertThat(updated.getUserRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("메모 업데이트")
    void updateNotes() {
        // given
        UserTechniqueProgress progress = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();

        // when
        progress.updateNotes("볼린저밴드 공부 완료!");
        progressRepository.save(progress);

        // then
        UserTechniqueProgress updated = progressRepository.findByUserIdAndTechniqueId(
                user.getId(), technique2.getId()).orElseThrow();
        assertThat(updated.getUserNotes()).isEqualTo("볼린저밴드 공부 완료!");
    }
}
