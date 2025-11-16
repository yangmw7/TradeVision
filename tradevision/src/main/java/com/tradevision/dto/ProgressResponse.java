package com.tradevision.dto;

import com.tradevision.constant.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 학습 진행도 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {

    /**
     * 진행도 ID
     */
    private Long id;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 기법 ID
     */
    private Long techniqueId;

    /**
     * 기법명
     */
    private String techniqueName;

    /**
     * 학습 상태
     */
    private ProgressStatus status;

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
     * 사용자 메모
     */
    private String userNotes;

    /**
     * 학습 완료 여부
     */
    private Boolean isCompleted;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}
