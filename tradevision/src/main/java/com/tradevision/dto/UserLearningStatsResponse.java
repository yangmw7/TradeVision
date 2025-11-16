package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 학습 통계 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLearningStatsResponse {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자 닉네임
     */
    private String nickname;

    /**
     * 완료한 콘텐츠 수
     */
    private Long completedContentCount;

    /**
     * 진행중인 콘텐츠 수
     */
    private Long inProgressContentCount;

    /**
     * 북마크한 콘텐츠 수
     */
    private Long bookmarkedContentCount;

    /**
     * 총 학습 시간 (초)
     */
    private Long totalTimeSpentSeconds;

    /**
     * 총 학습 시간 (분)
     */
    private Long totalTimeSpentMinutes;

    /**
     * 총 학습 시간 (시간)
     */
    private Long totalTimeSpentHours;

    /**
     * 평균 진행률 (0-100%)
     */
    private Double averageProgress;

    /**
     * 평균 퀴즈 점수 (0-100)
     */
    private Double averageQuizScore;

    /**
     * 학습 스트릭 (연속 학습 일수)
     */
    private Integer studyStreak;

    /**
     * 학습 레벨
     */
    private String learningLevel;

    /**
     * 다음 레벨까지 남은 콘텐츠 수
     */
    private Integer contentsToNextLevel;
}
