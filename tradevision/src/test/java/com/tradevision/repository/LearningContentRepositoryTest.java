package com.tradevision.repository;

import com.tradevision.config.TestJpaConfig;
import com.tradevision.constant.ContentType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.ContentModule;
import com.tradevision.entity.LearningContent;
import jakarta.persistence.EntityManager;
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
 * LearningContentRepository 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
class LearningContentRepositoryTest {

    @Autowired
    private LearningContentRepository contentRepository;

    @Autowired
    private ContentModuleRepository moduleRepository;

    @Autowired
    private EntityManager entityManager;

    private ContentModule module;
    private LearningContent content1;
    private LearningContent content2;
    private LearningContent content3;

    @BeforeEach
    void setUp() {
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
                .title("이동평균선이란?")
                .titleEn("What is Moving Average?")
                .summary("이동평균선의 기본 개념")
                .contentBody("# 이동평균선\n\n이동평균선은...")
                .contentType(ContentType.ARTICLE)
                .displayOrder(1)
                .estimatedDurationMinutes(10)
                .isFree(true)
                .isActive(true)
                .viewCount(100L)
                .likeCount(50L)
                .build();

        content2 = LearningContent.builder()
                .module(module)
                .title("이동평균선 실습")
                .titleEn("Moving Average Practice")
                .summary("이동평균선 실전 활용")
                .contentBody("# 실습\n\n실습 내용...")
                .contentType(ContentType.INTERACTIVE)
                .displayOrder(2)
                .estimatedDurationMinutes(20)
                .isFree(false)
                .isActive(true)
                .viewCount(50L)
                .likeCount(30L)
                .build();

        content3 = LearningContent.builder()
                .module(module)
                .title("이동평균선 퀴즈")
                .titleEn("Moving Average Quiz")
                .summary("학습 내용 점검")
                .contentBody("{\"questions\": []}")
                .contentType(ContentType.QUIZ)
                .displayOrder(3)
                .estimatedDurationMinutes(5)
                .isFree(true)
                .isActive(true)
                .viewCount(75L)
                .likeCount(40L)
                .build();

        contentRepository.save(content1);
        contentRepository.save(content2);
        contentRepository.save(content3);
    }

    @Test
    @DisplayName("모듈별 활성 콘텐츠 조회 - displayOrder 정렬")
    void findByModuleIdAndIsActiveTrue() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository.findByModuleIdAndIsActiveTrue(module.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.getContent().get(2).getDisplayOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("ID와 활성 상태로 콘텐츠 조회")
    void findByIdAndIsActiveTrue() {
        // when
        Optional<LearningContent> result = contentRepository.findByIdAndIsActiveTrue(content1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("이동평균선이란?");
    }

    @Test
    @DisplayName("콘텐츠 타입별 조회")
    void findByContentTypeAndIsActiveTrueOrderByCreatedAtDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository
                .findByContentTypeAndIsActiveTrueOrderByCreatedAtDesc(ContentType.ARTICLE, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContentType()).isEqualTo(ContentType.ARTICLE);
    }

    @Test
    @DisplayName("무료 콘텐츠 조회 - 조회수 내림차순")
    void findByIsFreeTrueAndIsActiveTrueOrderByViewCountDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository
                .findByIsFreeTrueAndIsActiveTrueOrderByViewCountDesc(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getViewCount()).isGreaterThanOrEqualTo(
                result.getContent().get(1).getViewCount());
    }

    @Test
    @DisplayName("인기 콘텐츠 조회 - Top N")
    void findTopByViewCount() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        // when
        List<LearningContent> result = contentRepository.findTopByViewCount(pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getViewCount()).isGreaterThanOrEqualTo(result.get(1).getViewCount());
    }

    @Test
    @DisplayName("키워드 검색 - 제목")
    void searchByKeyword_Title() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository.searchByKeyword("이동평균", pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).contains("이동평균");
    }

    @Test
    @DisplayName("키워드 검색 - 영어 제목")
    void searchByKeyword_TitleEn() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository.searchByKeyword("Moving", pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("키워드 검색 - 대소문자 무시")
    void searchByKeyword_CaseInsensitive() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<LearningContent> result = contentRepository.searchByKeyword("MOVING", pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("모듈별 활성 콘텐츠 수 카운트")
    void countByModuleIdAndIsActiveTrue() {
        // when
        long count = contentRepository.countByModuleIdAndIsActiveTrue(module.getId());

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("조회수 증가")
    void incrementViewCount() {
        // given
        Long originalViewCount = content1.getViewCount();

        // when
        contentRepository.incrementViewCount(content1.getId());
        contentRepository.flush();
        entityManager.clear();

        // then
        LearningContent updated = contentRepository.findById(content1.getId()).orElseThrow();
        assertThat(updated.getViewCount()).isEqualTo(originalViewCount + 1);
    }

    @Test
    @DisplayName("좋아요 수 증가")
    void incrementLikeCount() {
        // given
        Long originalLikeCount = content1.getLikeCount();

        // when
        contentRepository.incrementLikeCount(content1.getId());
        contentRepository.flush();
        entityManager.clear();

        // then
        LearningContent updated = contentRepository.findById(content1.getId()).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(originalLikeCount + 1);
    }

    @Test
    @DisplayName("좋아요 수 감소")
    void decrementLikeCount() {
        // given
        Long originalLikeCount = content1.getLikeCount();

        // when
        contentRepository.decrementLikeCount(content1.getId());
        contentRepository.flush();
        entityManager.clear();

        // then
        LearningContent updated = contentRepository.findById(content1.getId()).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(originalLikeCount - 1);
    }

    @Test
    @DisplayName("비활성 콘텐츠는 조회되지 않음")
    void inactiveContentNotFound() {
        // given
        LearningContent inactiveContent = LearningContent.builder()
                .module(module)
                .title("비활성 콘텐츠")
                .titleEn("Inactive Content")
                .summary("비활성화된 콘텐츠")
                .contentBody("내용")
                .contentType(ContentType.ARTICLE)
                .displayOrder(4)
                .estimatedDurationMinutes(10)
                .isFree(true)
                .isActive(false)
                .build();
        LearningContent saved = contentRepository.save(inactiveContent);

        // when
        Optional<LearningContent> result = contentRepository.findByIdAndIsActiveTrue(saved.getId());

        // then
        assertThat(result).isEmpty();
    }
}
