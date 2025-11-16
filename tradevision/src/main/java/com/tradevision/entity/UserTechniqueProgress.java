package com.tradevision.entity;

import com.tradevision.constant.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 매매기법 학습 진행도 엔티티
 */
@Entity
@Table(name = "user_technique_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "technique_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTechniqueProgress extends BaseTimeEntity {

    /**
     * 진행도 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 매매기법
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    private TradingTechnique technique;

    /**
     * 학습 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    /**
     * 진행률 (0-100%)
     */
    @Column(name = "progress_percentage", nullable = false)
    @Builder.Default
    private Integer progressPercentage = 0;

    /**
     * 북마크 여부
     */
    @Column(name = "is_bookmarked", nullable = false)
    @Builder.Default
    private Boolean isBookmarked = false;

    /**
     * 사용자 평점 (1-5)
     */
    @Column(name = "user_rating")
    private Integer userRating;

    /**
     * 사용자 메모
     */
    @Lob
    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;

    /**
     * 학습 완료 여부
     */
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    /**
     * 진행 상태 업데이트
     *
     * @param status            상태
     * @param progressPercentage 진행률
     */
    public void updateProgress(ProgressStatus status, Integer progressPercentage) {
        this.status = status;
        this.progressPercentage = progressPercentage;

        if (progressPercentage >= 100) {
            this.isCompleted = true;
            this.status = ProgressStatus.COMPLETED;
        }
    }

    /**
     * 북마크 토글
     */
    public void toggleBookmark() {
        this.isBookmarked = !this.isBookmarked;
    }

    /**
     * 평점 업데이트
     *
     * @param rating 평점 (1-5)
     */
    public void updateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1-5 사이여야 합니다");
        }
        this.userRating = rating;
    }

    /**
     * 메모 업데이트
     *
     * @param notes 메모
     */
    public void updateNotes(String notes) {
        this.userNotes = notes;
    }
}
