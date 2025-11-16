package com.tradevision.repository;

import com.tradevision.config.TestJpaConfig;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.ContentModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ContentModuleRepository 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
class ContentModuleRepositoryTest {

    @Autowired
    private ContentModuleRepository moduleRepository;

    private ContentModule beginnerModule;
    private ContentModule intermediateModule;
    private ContentModule advancedModule;

    @BeforeEach
    void setUp() {
        beginnerModule = ContentModule.builder()
                .title("이동평균선 기초")
                .titleEn("Moving Average Basics")
                .description("초보자를 위한 이동평균선 강의")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .displayOrder(1)
                .estimatedDurationMinutes(30)
                .isRequired(true)
                .isActive(true)
                .build();

        intermediateModule = ContentModule.builder()
                .title("볼린저 밴드 활용")
                .titleEn("Bollinger Bands Application")
                .description("중급자를 위한 볼린저 밴드")
                .difficultyLevel(InvestmentLevel.INTERMEDIATE)
                .category(TechniqueCategory.BOLLINGER_BANDS)
                .displayOrder(2)
                .estimatedDurationMinutes(45)
                .isRequired(false)
                .isActive(true)
                .build();

        advancedModule = ContentModule.builder()
                .title("MACD 심화")
                .titleEn("Advanced MACD")
                .description("고급자를 위한 MACD 전략")
                .difficultyLevel(InvestmentLevel.ADVANCED)
                .category(TechniqueCategory.MACD)
                .displayOrder(3)
                .estimatedDurationMinutes(60)
                .isRequired(false)
                .isActive(true)
                .build();

        moduleRepository.save(beginnerModule);
        moduleRepository.save(intermediateModule);
        moduleRepository.save(advancedModule);
    }

    @Test
    @DisplayName("활성 모듈 전체 조회 - displayOrder 정렬")
    void findByIsActiveTrueOrderByDisplayOrderAsc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentModule> result = moduleRepository.findByIsActiveTrueOrderByDisplayOrderAsc(pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.getContent().get(2).getDisplayOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("난이도별 모듈 조회")
    void findByDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentModule> result = moduleRepository
                .findByDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(InvestmentLevel.BEGINNER, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDifficultyLevel()).isEqualTo(InvestmentLevel.BEGINNER);
    }

    @Test
    @DisplayName("카테고리별 모듈 조회")
    void findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentModule> result = moduleRepository
                .findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(TechniqueCategory.MOVING_AVERAGE, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(TechniqueCategory.MOVING_AVERAGE);
    }

    @Test
    @DisplayName("난이도 + 카테고리 필터링")
    void findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByDisplayOrderAsc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentModule> result = moduleRepository
                .findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByDisplayOrderAsc(
                        InvestmentLevel.BEGINNER, TechniqueCategory.MOVING_AVERAGE, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDifficultyLevel()).isEqualTo(InvestmentLevel.BEGINNER);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(TechniqueCategory.MOVING_AVERAGE);
    }

    @Test
    @DisplayName("필수 모듈 조회")
    void findByIsRequiredTrueAndIsActiveTrueOrderByDisplayOrderAsc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentModule> result = moduleRepository
                .findByIsRequiredTrueAndIsActiveTrueOrderByDisplayOrderAsc(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsRequired()).isTrue();
    }

    @Test
    @DisplayName("ID와 활성 상태로 조회")
    void findByIdAndIsActiveTrue() {
        // when
        Optional<ContentModule> result = moduleRepository.findByIdAndIsActiveTrue(beginnerModule.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("이동평균선 기초");
    }

    @Test
    @DisplayName("카테고리별 모듈 수 카운트")
    void countByCategoryAndIsActiveTrue() {
        // when
        long count = moduleRepository.countByCategoryAndIsActiveTrue(TechniqueCategory.MOVING_AVERAGE);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("비활성 모듈은 조회되지 않음")
    void inactiveModuleNotFound() {
        // given
        ContentModule inactiveModule = ContentModule.builder()
                .title("비활성 모듈")
                .titleEn("Inactive Module")
                .description("비활성화된 모듈")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.RSI)
                .displayOrder(4)
                .estimatedDurationMinutes(30)
                .isRequired(false)
                .isActive(false)
                .build();
        ContentModule saved = moduleRepository.save(inactiveModule);

        // when
        Optional<ContentModule> result = moduleRepository.findByIdAndIsActiveTrue(saved.getId());

        // then
        assertThat(result).isEmpty();
    }
}
