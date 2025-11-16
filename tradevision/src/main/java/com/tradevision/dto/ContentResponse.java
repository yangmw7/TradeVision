package com.tradevision.dto;

import com.tradevision.constant.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 학습 콘텐츠 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentResponse {

    /**
     * 콘텐츠 ID
     */
    private Long id;

    /**
     * 모듈 ID
     */
    private Long moduleId;

    /**
     * 모듈명
     */
    private String moduleName;

    /**
     * 콘텐츠 제목
     */
    private String title;

    /**
     * 콘텐츠 제목 (영문)
     */
    private String titleEn;

    /**
     * 콘텐츠 요약
     */
    private String summary;

    /**
     * 콘텐츠 본문
     */
    private String contentBody;

    /**
     * 콘텐츠 타입
     */
    private ContentType contentType;

    /**
     * 콘텐츠 순서
     */
    private Integer displayOrder;

    /**
     * 예상 학습 시간 (분)
     */
    private Integer estimatedDurationMinutes;

    /**
     * 비디오 URL
     */
    private String videoUrl;

    /**
     * 이미지 URL 목록
     */
    private List<String> imageUrls;

    /**
     * 무료 콘텐츠 여부
     */
    private Boolean isFree;

    /**
     * 조회수
     */
    private Long viewCount;

    /**
     * 좋아요 수
     */
    private Long likeCount;

    /**
     * 퀴즈 데이터
     */
    private String quizData;

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
    private UserContentProgressInfo userProgress;

    /**
     * 사용자 콘텐츠 진행도 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserContentProgressInfo {
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
         * 학습 시간 (초)
         */
        private Long totalTimeSpentSeconds;

        /**
         * 퀴즈 점수
         */
        private Integer quizScore;

        /**
         * 마지막 액세스 시간
         */
        private LocalDateTime lastAccessedAt;
    }
}
