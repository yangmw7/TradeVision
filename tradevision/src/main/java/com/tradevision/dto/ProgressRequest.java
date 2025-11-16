package com.tradevision.dto;

import com.tradevision.constant.ProgressStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학습 진행도 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressRequest {

    /**
     * 학습 상태
     */
    @NotNull(message = "학습 상태는 필수입니다")
    private ProgressStatus status;

    /**
     * 진행률 (0-100%)
     */
    @NotNull(message = "진행률은 필수입니다")
    @Min(value = 0, message = "진행률은 0 이상이어야 합니다")
    @Max(value = 100, message = "진행률은 100 이하여야 합니다")
    private Integer progressPercentage;

    /**
     * 북마크 여부 (선택)
     */
    private Boolean isBookmarked;

    /**
     * 사용자 평점 (1-5, 선택)
     */
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    private Integer userRating;

    /**
     * 사용자 메모 (선택)
     */
    private String userNotes;
}
