package com.tradevision.dto.response;

import com.tradevision.constant.CandleType;
import com.tradevision.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 차트 분석 결과 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartAnalysisResponse {

    /**
     * 분석 ID
     */
    private Long analysisId;

    /**
     * 종목 코드
     */
    private String stockCode;

    /**
     * 종목명
     */
    private String stockName;

    /**
     * 캔들 타입
     */
    private CandleType candleType;

    /**
     * 이미지 경로 (URL)
     */
    private String imagePath;

    /**
     * AI 분석 결과
     */
    private AnalysisResult analysisResult;

    /**
     * 사용자 피드백
     */
    private FeedbackType feedback;

    /**
     * 분석 일시
     */
    private LocalDateTime createdAt;

    /**
     * AI 분석 결과 상세 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisResult {

        /**
         * 감지된 차트 패턴
         */
        private String pattern;

        /**
         * 현재 추세 (상승/하락/횡보)
         */
        private String trend;

        /**
         * 지지선 가격
         */
        private String supportLevel;

        /**
         * 저항선 가격
         */
        private String resistanceLevel;

        /**
         * 거래량 분석
         */
        private String volumeAnalysis;

        /**
         * 매매 의견 (매수/매도/관망)
         */
        private String tradingOpinion;

        /**
         * 종합 의견 (상세 설명)
         */
        private String summary;

        /**
         * 주요 포인트 (리스트)
         */
        private java.util.List<String> keyPoints;

        /**
         * 위험도 (낮음/보통/높음)
         */
        private String riskLevel;
    }
}
