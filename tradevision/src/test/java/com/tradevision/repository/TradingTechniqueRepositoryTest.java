package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.TradingTechnique;
import jakarta.persistence.EntityManager;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TradingTechniqueRepository 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EnableJpaAuditing
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class TradingTechniqueRepositoryTest {

    @Autowired
    private TradingTechniqueRepository techniqueRepository;

    @Autowired
    private EntityManager entityManager;

    private TradingTechnique technique1;
    private TradingTechnique technique2;
    private TradingTechnique technique3;

    @BeforeEach
    void setUp() {
        techniqueRepository.deleteAll();

        // 초보자 - 이동평균선
        technique1 = TradingTechnique.builder()
                .name("이동평균선 교차 전략")
                .nameEn("Moving Average Crossover")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.MOVING_AVERAGE)
                .summary("단기와 장기 이동평균선의 교차를 이용한 매매 기법")
                .description("이동평균선 교차는 가장 기본적인 매매 기법입니다.")
                .usageGuide("5일선이 20일선을 상향 돌파하면 매수, 하향 돌파하면 매도")
                .exampleScenario("골든크로스 발생 시 매수 진입")
                .advantages("명확한 매매 신호, 초보자도 쉽게 이해")
                .disadvantages("횡보장에서는 속임수 신호 발생")
                .riskLevel(2)
                .isActive(true)
                .build();

        // 중급자 - 볼린저밴드
        technique2 = TradingTechnique.builder()
                .name("볼린저밴드 전략")
                .nameEn("Bollinger Bands Strategy")
                .difficultyLevel(InvestmentLevel.INTERMEDIATE)
                .category(TechniqueCategory.BOLLINGER_BANDS)
                .summary("변동성 기반 매매 기법")
                .description("볼린저밴드는 가격의 변동성을 측정하는 지표입니다.")
                .usageGuide("하단 밴드 터치 시 매수, 상단 밴드 터치 시 매도")
                .exampleScenario("밴드폭 축소 후 확대 시 추세 전환 예측")
                .advantages("변동성 측정 가능, 과매수/과매도 판단")
                .disadvantages("강한 추세장에서는 밴드 이탈 지속")
                .riskLevel(3)
                .isActive(true)
                .build();

        // 고급자 - MACD
        technique3 = TradingTechnique.builder()
                .name("MACD 다이버전스")
                .nameEn("MACD Divergence")
                .difficultyLevel(InvestmentLevel.ADVANCED)
                .category(TechniqueCategory.MACD)
                .summary("추세 전환 예측 기법")
                .description("MACD와 가격의 다이버전스로 추세 전환을 예측합니다.")
                .usageGuide("가격 신고가 대비 MACD 낮아지면 약세 다이버전스")
                .exampleScenario("하락 추세에서 강세 다이버전스 발견 시 반전 매수")
                .advantages("추세 전환 조기 포착")
                .disadvantages("다이버전스 발생 후에도 추세 지속 가능")
                .riskLevel(4)
                .isActive(true)
                .build();

        techniqueRepository.saveAll(List.of(technique1, technique2, technique3));
    }

    @Test
    @DisplayName("활성화된 기법 조회")
    void findByIsActiveTrueOrderByCreatedAtDesc() {
        // when
        Page<TradingTechnique> result = techniqueRepository.findByIsActiveTrueOrderByCreatedAtDesc(
                PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting("isActive").containsOnly(true);
    }

    @Test
    @DisplayName("난이도별 기법 조회")
    void findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc() {
        // when
        Page<TradingTechnique> result = techniqueRepository
                .findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
                        InvestmentLevel.BEGINNER, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("이동평균선 교차 전략");
        assertThat(result.getContent().get(0).getDifficultyLevel()).isEqualTo(InvestmentLevel.BEGINNER);
    }

    @Test
    @DisplayName("카테고리별 기법 조회")
    void findByCategoryAndIsActiveTrueOrderByViewCountDesc() {
        // when
        Page<TradingTechnique> result = techniqueRepository
                .findByCategoryAndIsActiveTrueOrderByViewCountDesc(
                        TechniqueCategory.MOVING_AVERAGE, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(TechniqueCategory.MOVING_AVERAGE);
    }

    @Test
    @DisplayName("난이도와 카테고리로 기법 조회")
    void findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByRecommendationCountDesc() {
        // when
        Page<TradingTechnique> result = techniqueRepository
                .findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByRecommendationCountDesc(
                        InvestmentLevel.INTERMEDIATE, TechniqueCategory.BOLLINGER_BANDS, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("볼린저밴드 전략");
    }

    @Test
    @DisplayName("키워드로 기법 검색 - 한글")
    void searchByKeyword_Korean() {
        // when
        Page<TradingTechnique> result = techniqueRepository.searchByKeyword("이동평균", PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("이동평균");
    }

    @Test
    @DisplayName("키워드로 기법 검색 - 영문")
    void searchByKeyword_English() {
        // when
        Page<TradingTechnique> result = techniqueRepository.searchByKeyword("MACD", PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNameEn()).contains("MACD");
    }

    @Test
    @DisplayName("인기 기법 조회 (조회수 기준)")
    void findTopByViewCount() {
        // given
        technique1.incrementViewCount();
        technique1.incrementViewCount();
        technique1.incrementViewCount();
        technique2.incrementViewCount();
        techniqueRepository.saveAll(List.of(technique1, technique2));

        // when
        List<TradingTechnique> result = techniqueRepository.findTopByViewCount(PageRequest.of(0, 2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getViewCount()).isGreaterThanOrEqualTo(result.get(1).getViewCount());
    }

    @Test
    @DisplayName("추천 기법 조회 (추천수 기준)")
    void findTopByRecommendationCount() {
        // given
        technique2.incrementRecommendationCount();
        technique2.incrementRecommendationCount();
        technique3.incrementRecommendationCount();
        techniqueRepository.saveAll(List.of(technique2, technique3));

        // when
        List<TradingTechnique> result = techniqueRepository.findTopByRecommendationCount(PageRequest.of(0, 2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRecommendationCount())
                .isGreaterThanOrEqualTo(result.get(1).getRecommendationCount());
    }

    @Test
    @DisplayName("조회수 증가")
    void incrementViewCount() {
        // given
        Long techniqueId = technique1.getId();
        Long initialCount = technique1.getViewCount();

        // when
        techniqueRepository.incrementViewCount(techniqueId);
        techniqueRepository.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // then
        TradingTechnique updated = techniqueRepository.findById(techniqueId).orElseThrow();
        assertThat(updated.getViewCount()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("추천수 증가")
    void incrementRecommendationCount() {
        // given
        Long techniqueId = technique1.getId();
        Long initialCount = technique1.getRecommendationCount();

        // when
        techniqueRepository.incrementRecommendationCount(techniqueId);
        techniqueRepository.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // then
        TradingTechnique updated = techniqueRepository.findById(techniqueId).orElseThrow();
        assertThat(updated.getRecommendationCount()).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("ID와 활성화 여부로 조회 - 성공")
    void findByIdAndIsActiveTrue_Success() {
        // when
        Optional<TradingTechnique> result = techniqueRepository.findByIdAndIsActiveTrue(technique1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("이동평균선 교차 전략");
    }

    @Test
    @DisplayName("ID와 활성화 여부로 조회 - 비활성 기법")
    void findByIdAndIsActiveTrue_Inactive() {
        // given
        TradingTechnique inactiveTechnique = TradingTechnique.builder()
                .name("비활성 기법")
                .nameEn("Inactive")
                .difficultyLevel(InvestmentLevel.BEGINNER)
                .category(TechniqueCategory.RSI)
                .summary("테스트")
                .description("테스트")
                .riskLevel(1)
                .isActive(false)
                .build();
        techniqueRepository.save(inactiveTechnique);

        // when
        Optional<TradingTechnique> result = techniqueRepository.findByIdAndIsActiveTrue(inactiveTechnique.getId());

        // then
        assertThat(result).isEmpty();
    }
}
