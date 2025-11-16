package com.tradevision.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 학습 콘텐츠 진행도 엔티티
 */
@Entity
@Table(name = "user_content_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "content_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserContentProgress extends BaseTimeEntity {

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
     * 학습 콘텐츠
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private LearningContent content;

    /**
     * 완료 여부
     */
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    /**
     * 진행률 (0-100%)
     */
    @Column(name = "progress_percentage", nullable = false)
    @Builder.Default
    private Integer progressPercentage = 0;

    /**
     * 좋아요 여부
     */
    @Column(name = "is_liked", nullable = false)
    @Builder.Default
    private Boolean isLiked = false;

    /**
     * 북마크 여부
     */
    @Column(name = "is_bookmarked", nullable = false)
    @Builder.Default
    private Boolean isBookmarked = false;

    /**
     * 총 학습 시간 (초)
     */
    @Column(name = "total_time_spent_seconds", nullable = false)
    @Builder.Default
    private Long totalTimeSpentSeconds = 0L;

    /**
     * 마지막 액세스 시간
     */
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /**
     * 완료 시간
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 퀴즈 점수 (퀴즈인 경우, 0-100)
     */
    @Column(name = "quiz_score")
    private Integer quizScore;

    /**
     * 퀴즈 응답 데이터 (JSON)
     */
    @Lob
    @Column(name = "quiz_answers", columnDefinition = "TEXT")
    private String quizAnswers;

    /**
     * 사용자 노트/메모
     */
    @Lob
    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;

    /**
     * 진행도 업데이트
     *
     * @param progressPercentage 진행률
     */
    public void updateProgress(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
        this.lastAccessedAt = LocalDateTime.now();

        if (progressPercentage >= 100 && !this.isCompleted) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 학습 시간 추가
     *
     * @param seconds 학습 시간 (초)
     */
    public void addTimeSpent(Long seconds) {
        this.totalTimeSpentSeconds += seconds;
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * 좋아요 토글
     */
    public void toggleLike() {
        this.isLiked = !this.isLiked;
    }

    /**
     * 북마크 토글
     */
    public void toggleBookmark() {
        this.isBookmarked = !this.isBookmarked;
    }

    /**
     * 퀴즈 점수 업데이트
     *
     * @param score       점수 (0-100)
     * @param quizAnswers 퀴즈 응답 JSON
     */
    public void updateQuizScore(Integer score, String quizAnswers) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("퀴즈 점수는 0-100 사이여야 합니다");
        }
        this.quizScore = score;
        this.quizAnswers = quizAnswers;
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * 노트 업데이트
     *
     * @param notes 사용자 노트
     */
    public void updateNotes(String notes) {
        this.userNotes = notes;
    }

    /**
     * 완료 처리
     */
    public void markAsCompleted() {
        this.isCompleted = true;
        this.progressPercentage = 100;
        this.completedAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }
}
