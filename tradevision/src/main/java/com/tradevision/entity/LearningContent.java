package com.tradevision.entity;

import com.tradevision.constant.ContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학습 콘텐츠 엔티티
 * 실제 학습 자료 (문서, 비디오, 퀴즈 등)
 */
@Entity
@Table(name = "learning_contents")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningContent extends BaseTimeEntity {

    /**
     * 콘텐츠 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 모듈
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ContentModule module;

    /**
     * 콘텐츠 제목
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 콘텐츠 제목 (영문)
     */
    @Column(name = "title_en", length = 200)
    private String titleEn;

    /**
     * 콘텐츠 요약
     */
    @Column(name = "summary", length = 500)
    private String summary;

    /**
     * 콘텐츠 본문 (마크다운 형식)
     */
    @Lob
    @Column(name = "content_body", nullable = false, columnDefinition = "TEXT")
    private String contentBody;

    /**
     * 콘텐츠 타입 (문서, 비디오, 퀴즈 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    /**
     * 콘텐츠 순서 (모듈 내 정렬)
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    /**
     * 예상 학습 시간 (분)
     */
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    /**
     * 비디오 URL (비디오 타입인 경우)
     */
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    /**
     * 이미지 URL 목록 (쉼표로 구분)
     */
    @Lob
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 무료 콘텐츠 여부
     */
    @Column(name = "is_free", nullable = false)
    @Builder.Default
    private Boolean isFree = true;

    /**
     * 조회수
     */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * 좋아요 수
     */
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    /**
     * 퀴즈 데이터 (JSON 형식, 퀴즈 타입인 경우)
     */
    @Lob
    @Column(name = "quiz_data", columnDefinition = "TEXT")
    private String quizData;

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
