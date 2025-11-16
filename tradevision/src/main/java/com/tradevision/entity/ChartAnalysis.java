package com.tradevision.entity;

import com.tradevision.constant.CandleType;
import com.tradevision.constant.FeedbackType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 차트 분석 히스토리 엔티티
 * AI 차트 분석 결과 저장
 */
@Entity
@Table(name = "chart_analyses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChartAnalysis {

    /**
     * 분석 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 (FK)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 종목 코드 (6자리)
     */
    @Column(name = "stock_code", length = 10)
    private String stockCode;

    /**
     * 종목명
     */
    @Column(name = "stock_name", length = 100)
    private String stockName;

    /**
     * 캔들 타입 (봉 종류)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "candle_type", nullable = false)
    private CandleType candleType;

    /**
     * 업로드된 차트 이미지 경로
     */
    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    /**
     * AI 분석 결과 (JSON)
     * 패턴, 지지/저항선, 매매 의견 등
     */
    @Column(name = "analysis_result", nullable = false, columnDefinition = "JSON")
    private String analysisResult;

    /**
     * 사용자 피드백 (성공/실패/없음)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback", nullable = false)
    @Builder.Default
    private FeedbackType feedback = FeedbackType.NONE;

    /**
     * 분석 일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 생성 시 분석 일시 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 피드백 업데이트
     *
     * @param feedbackType 사용자 피드백 (성공/실패)
     */
    public void updateFeedback(FeedbackType feedbackType) {
        this.feedback = feedbackType;
    }
}
