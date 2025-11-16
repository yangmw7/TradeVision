package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 맞춤형 기법 추천 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {

    /**
     * 추천 기법 목록
     */
    private List<TechniqueResponse> recommendedTechniques;

    /**
     * 추천 이유
     */
    private String reason;

    /**
     * 사용자 투자 수준
     */
    private String userLevel;

    /**
     * 사용자 완료 기법 수
     */
    private long completedCount;

    /**
     * 사용자 평균 진행률
     */
    private Double averageProgress;

    /**
     * 추천 메시지
     */
    private String message;
}
