package com.tradevision.dto;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 콘텐츠 모듈 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {

    /**
     * 모듈 ID
     */
    private Long id;

    /**
     * 모듈명 (한글)
     */
    private String title;

    /**
     * 모듈명 (영문)
     */
    private String titleEn;

    /**
     * 모듈 설명
     */
    private String description;

    /**
     * 난이도
     */
    private InvestmentLevel difficultyLevel;

    /**
     * 카테고리
     */
    private TechniqueCategory category;

    /**
     * 모듈 순서
     */
    private Integer displayOrder;

    /**
     * 예상 학습 시간 (분)
     */
    private Integer estimatedDurationMinutes;

    /**
     * 썸네일 URL
     */
    private String thumbnailUrl;

    /**
     * 필수 모듈 여부
     */
    private Boolean isRequired;

    /**
     * 모듈 내 콘텐츠 수
     */
    private Long contentCount;

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
    private UserModuleProgress userProgress;

    /**
     * 사용자 모듈 진행도 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserModuleProgress {
        /**
         * 완료한 콘텐츠 수
         */
        private Long completedContentCount;

        /**
         * 전체 콘텐츠 수
         */
        private Long totalContentCount;

        /**
         * 진행률 (0-100%)
         */
        private Integer progressPercentage;

        /**
         * 완료 여부
         */
        private Boolean isCompleted;
    }
}
