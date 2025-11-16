package com.tradevision.dto;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 매매기법 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechniqueResponse {

    /**
     * 기법 ID
     */
    private Long id;

    /**
     * 기법명 (한글)
     */
    private String name;

    /**
     * 기법명 (영문)
     */
    private String nameEn;

    /**
     * 난이도
     */
    private InvestmentLevel difficultyLevel;

    /**
     * 카테고리
     */
    private TechniqueCategory category;

    /**
     * 간단한 설명
     */
    private String summary;

    /**
     * 상세 설명
     */
    private String description;

    /**
     * 사용 방법
     */
    private String usageGuide;

    /**
     * 예시 시나리오
     */
    private String exampleScenario;

    /**
     * 장점
     */
    private String advantages;

    /**
     * 단점
     */
    private String disadvantages;

    /**
     * 위험도 (1-5)
     */
    private Integer riskLevel;

    /**
     * 조회수
     */
    private Long viewCount;

    /**
     * 추천수
     */
    private Long recommendationCount;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * 사용자 진행 정보 (로그인한 경우)
     */
    private UserProgressInfo userProgress;

    /**
     * 사용자 진행 정보 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProgressInfo {
        /**
         * 학습 상태
         */
        private String status;

        /**
         * 진행률 (0-100%)
         */
        private Integer progressPercentage;

        /**
         * 북마크 여부
         */
        private Boolean isBookmarked;

        /**
         * 사용자 평점 (1-5)
         */
        private Integer userRating;

        /**
         * 학습 완료 여부
         */
        private Boolean isCompleted;
    }
}
