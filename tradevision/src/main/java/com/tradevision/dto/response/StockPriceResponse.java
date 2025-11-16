package com.tradevision.dto.response;

import com.tradevision.constant.CandleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주식 시세 응답 DTO
 * 실시간 주식 가격 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPriceResponse {

    /**
     * 종목 코드 (6자리, 예: 005930 = 삼성전자)
     */
    private String stockCode;

    /**
     * 종목명
     */
    private String stockName;

    /**
     * 현재가
     */
    private BigDecimal currentPrice;

    /**
     * 전일 대비 변동가
     */
    private BigDecimal priceChange;

    /**
     * 전일 대비 변동률 (%)
     */
    private BigDecimal changeRate;

    /**
     * 거래량
     */
    private Long volume;

    /**
     * 시가
     */
    private BigDecimal openPrice;

    /**
     * 고가
     */
    private BigDecimal highPrice;

    /**
     * 저가
     */
    private BigDecimal lowPrice;

    /**
     * 캔들 타입 (분봉, 일봉 등)
     */
    private CandleType candleType;

    /**
     * 데이터 시간
     */
    private LocalDateTime timestamp;

    /**
     * 전일 종가
     */
    private BigDecimal previousClose;
}
