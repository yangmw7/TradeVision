package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 콘텐츠 진행도 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentProgressResponse {

    /**
     * 진행도 ID
     */
    private Long id;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 콘텐츠 ID
     */
    private Long contentId;

    /**
     * 콘텐츠 제목
     */
    private String contentTitle;

    /**
     * 모듈 ID
     */
    private Long moduleId;

    /**
     * 모듈명
     */
    private String moduleName;

    /**
     * 완료 여부
     */
    private Boolean isCompleted;

    /**
     * 진행률 (0-100%)
     */
    private Integer progressPercentage;

    /**
     * 좋아요 여부
     */
    private Boolean isLiked;

    /**
     * 북마크 여부
     */
    private Boolean isBookmarked;

    /**
     * 총 학습 시간 (초)
     */
    private Long totalTimeSpentSeconds;

    /**
     * 퀴즈 점수
     */
    private Integer quizScore;

    /**
     * 퀴즈 응답 데이터
     */
    private String quizAnswers;

    /**
     * 사용자 노트
     */
    private String userNotes;

    /**
     * 마지막 액세스 시간
     */
    private LocalDateTime lastAccessedAt;

    /**
     * 완료 시간
     */
    private LocalDateTime completedAt;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}
