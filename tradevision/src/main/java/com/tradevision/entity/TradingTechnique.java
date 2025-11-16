package com.tradevision.entity;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매매기법 엔티티
 * 다양한 주식 매매 전략과 기법 정보
 */
@Entity
@Table(name = "trading_techniques")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingTechnique extends BaseTimeEntity {

    /**
     * 기법 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 기법명 (한글)
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 기법명 (영문)
     */
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /**
     * 난이도 (초보자/중급자/고급자)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private InvestmentLevel difficultyLevel;

    /**
     * 카테고리 (추세추종, 모멘텀, 평균회귀 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TechniqueCategory category;

    /**
     * 간단한 설명
     */
    @Column(name = "summary", nullable = false, length = 500)
    private String summary;

    /**
     * 상세 설명
     */
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * 사용 방법
     */
    @Lob
    @Column(name = "usage_guide", columnDefinition = "TEXT")
    private String usageGuide;

    /**
     * 예시 시나리오
     */
    @Lob
    @Column(name = "example_scenario", columnDefinition = "TEXT")
    private String exampleScenario;

    /**
     * 장점
     */
    @Column(name = "advantages", length = 1000)
    private String advantages;

    /**
     * 단점
     */
    @Column(name = "disadvantages", length = 1000)
    private String disadvantages;

    /**
     * 위험도 (1-5, 1이 가장 낮음)
     */
    @Column(name = "risk_level", nullable = false)
    private Integer riskLevel;

    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 조회수
     */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * 추천수
     */
    @Column(name = "recommendation_count", nullable = false)
    @Builder.Default
    private Long recommendationCount = 0L;

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 추천수 증가
     */
    public void incrementRecommendationCount() {
        this.recommendationCount++;
    }
}
