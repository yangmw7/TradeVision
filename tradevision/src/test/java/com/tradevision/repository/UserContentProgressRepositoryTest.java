package com.tradevision.repository;

import com.tradevision.config.TestJpaConfig;
import com.tradevision.constant.ContentType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.ContentModule;
import com.tradevision.entity.LearningContent;
import com.tradevision.entity.User;
import com.tradevision.entity.UserContentProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserContentProgressRepository 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
class UserContentProgressRepositoryTest {

    @Autowired
    private UserContentProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningContentRepository contentRepository;

    @Autowired
    private ContentModuleRepository moduleRepository;

    private User user;
    private ContentModule module;
    private LearningContent content1;
    private LearningContent content2;
    private LearningContent content3;
    private UserContentProgress progress1;
    private UserContentProgress progress2;
    private UserContentProgress progress3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스트유저")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        user = userRepository.save(user);

        module = ContentModule.builder()
                .title("이동평균선 학습")
                .titleEn("Moving Average Learning")
                .description("이동평균선 기초부터 실전까지")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .displayOrder(1)
                .estimatedDurationMinutes(30)
                .isRequired(true)
                .isActive(true)
                .build();
        module = moduleRepository.save(module);

        content1 = LearningContent.builder()
                .module(module)
                .title("콘텐츠 1")
                .titleEn("Content 1")
                .summary("요약 1")
                .contentBody("내용 1")
                .contentType(ContentType.ARTICLE)
                .displayOrder(1)
                .estimatedDurationMinutes(10)
                .isFree(true)
                .isActive(true)
                .build();

        content2 = LearningContent.builder()
                .module(module)
                .title("콘텐츠 2")
                .titleEn("Content 2")
                .summary("요약 2")
                .contentBody("내용 2")
                .contentType(ContentType.VIDEO)
                .displayOrder(2)
                .estimatedDurationMinutes(20)
                .isFree(true)
                .isActive(true)
                .build();

        content3 = LearningContent.builder()
                .module(module)
                .title("콘텐츠 3")
                .titleEn("Content 3")
                .summary("요약 3")
                .contentBody("내용 3")
                .contentType(ContentType.QUIZ)
                .displayOrder(3)
                .estimatedDurationMinutes(5)
                .isFree(true)
                .isActive(true)
                .build();

        content1 = contentRepository.save(content1);
        content2 = contentRepository.save(content2);
        content3 = contentRepository.save(content3);

        // progress1: 완료
        progress1 = UserContentProgress.builder()
                .user(user)
                .content(content1)
                .isCompleted(true)
                .progressPercentage(100)
                .totalTimeSpentSeconds(600L)
                .quizScore(90)
                .isLiked(true)
                .isBookmarked(false)
                .build();
        progress1.markAsCompleted();

        // progress2: 진행중
        progress2 = UserContentProgress.builder()
                .user(user)
                .content(content2)
                .isCompleted(false)
                .progressPercentage(50)
                .totalTimeSpentSeconds(300L)
                .isLiked(false)
                .isBookmarked(true)
                .build();

        // progress3: 북마크만
        progress3 = UserContentProgress.builder()
                .user(user)
                .content(content3)
                .isCompleted(false)
                .progressPercentage(0)
                .totalTimeSpentSeconds(0L)
                .isLiked(false)
                .isBookmarked(true)
                .build();

        progressRepository.save(progress1);
        progressRepository.save(progress2);
        progressRepository.save(progress3);
    }

    @Test
    @DisplayName("사용자 ID와 콘텐츠 ID로 진행도 조회")
    void findByUserIdAndContentId() {
        // when
        Optional<UserContentProgress> result = progressRepository
                .findByUserIdAndContentId(user.getId(), content1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getProgressPercentage()).isEqualTo(100);
    }

    @Test
    @DisplayName("사용자별 모듈의 진행도 목록 조회")
    void findByUserIdAndModuleId() {
        // when
        List<UserContentProgress> result = progressRepository
                .findByUserIdAndModuleId(user.getId(), module.getId());

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("사용자 진행도 목록 - 최근 접근 순")
    void findByUserIdOrderByLastAccessedAtDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<UserContentProgress> result = progressRepository
                .findByUserIdOrderByLastAccessedAtDesc(user.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("완료된 콘텐츠 조회")
    void findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<UserContentProgress> result = progressRepository
                .findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(user.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsCompleted()).isTrue();
    }

    @Test
    @DisplayName("진행중인 콘텐츠 조회")
    void findInProgressContents() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<UserContentProgress> result = progressRepository.findInProgressContents(user.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProgressPercentage()).isGreaterThan(0);
        assertThat(result.getContent().get(0).getIsCompleted()).isFalse();
    }

    @Test
    @DisplayName("북마크된 콘텐츠 조회")
    void findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<UserContentProgress> result = progressRepository
                .findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(user.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getIsBookmarked()).isTrue();
    }

    @Test
    @DisplayName("완료한 콘텐츠 수 카운트")
    void countByUserIdAndIsCompletedTrue() {
        // when
        long count = progressRepository.countByUserIdAndIsCompletedTrue(user.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("총 학습 시간 계산")
    void calculateTotalTimeSpent() {
        // when
        Long totalSeconds = progressRepository.calculateTotalTimeSpent(user.getId());

        // then
        assertThat(totalSeconds).isEqualTo(900L); // 600 + 300 + 0
    }

    @Test
    @DisplayName("평균 진행률 계산")
    void calculateAverageProgress() {
        // when
        Double averageProgress = progressRepository.calculateAverageProgress(user.getId());

        // then
        assertThat(averageProgress).isEqualTo(50.0); // (100 + 50 + 0) / 3
    }

    @Test
    @DisplayName("평균 퀴즈 점수 계산")
    void calculateAverageQuizScore() {
        // when
        Double averageScore = progressRepository.calculateAverageQuizScore(user.getId());

        // then
        assertThat(averageScore).isEqualTo(90.0); // progress1만 점수가 있음
    }

    @Test
    @DisplayName("사용자-콘텐츠 유니크 제약 조건")
    void uniqueConstraint_UserAndContent() {
        // given
        UserContentProgress duplicate = UserContentProgress.builder()
                .user(user)
                .content(content1)
                .build();

        // when & then
        // 동일한 user와 content로 또 다른 진행도를 저장하려고 하면 예외 발생
        try {
            progressRepository.saveAndFlush(duplicate);
            assertThat(false).as("Should throw exception").isTrue();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("진행도 업데이트 - 100% 도달시 자동 완료")
    void updateProgress_AutoComplete() {
        // given
        progress2.updateProgress(100);

        // when
        UserContentProgress saved = progressRepository.save(progress2);

        // then
        assertThat(saved.getIsCompleted()).isTrue();
        assertThat(saved.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("좋아요 토글")
    void toggleLike() {
        // given
        boolean originalLiked = progress2.getIsLiked();

        // when
        progress2.toggleLike();
        UserContentProgress saved = progressRepository.save(progress2);

        // then
        assertThat(saved.getIsLiked()).isEqualTo(!originalLiked);
    }

    @Test
    @DisplayName("북마크 토글")
    void toggleBookmark() {
        // given
        boolean originalBookmarked = progress1.getIsBookmarked();

        // when
        progress1.toggleBookmark();
        UserContentProgress saved = progressRepository.save(progress1);

        // then
        assertThat(saved.getIsBookmarked()).isEqualTo(!originalBookmarked);
    }

    @Test
    @DisplayName("학습 시간 추가")
    void addTimeSpent() {
        // given
        Long originalTime = progress1.getTotalTimeSpentSeconds();

        // when
        progress1.addTimeSpent(120L);
        UserContentProgress saved = progressRepository.save(progress1);

        // then
        assertThat(saved.getTotalTimeSpentSeconds()).isEqualTo(originalTime + 120);
    }
}
