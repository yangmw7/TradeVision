package com.tradevision.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 콘텐츠 진행도 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentProgressRequest {

    /**
     * 진행률 (0-100%)
     */
    @NotNull(message = "진행률은 필수입니다")
    @Min(value = 0, message = "진행률은 0 이상이어야 합니다")
    @Max(value = 100, message = "진행률은 100 이하여야 합니다")
    private Integer progressPercentage;

    /**
     * 학습 시간 추가 (초)
     */
    @Min(value = 0, message = "학습 시간은 0 이상이어야 합니다")
    private Long timeSpentSeconds;

    /**
     * 퀴즈 점수 (0-100, 퀴즈인 경우)
     */
    @Min(value = 0, message = "퀴즈 점수는 0 이상이어야 합니다")
    @Max(value = 100, message = "퀴즈 점수는 100 이하여야 합니다")
    private Integer quizScore;

    /**
     * 퀴즈 응답 데이터 (JSON)
     */
    private String quizAnswers;

    /**
     * 사용자 노트
     */
    private String userNotes;
}
