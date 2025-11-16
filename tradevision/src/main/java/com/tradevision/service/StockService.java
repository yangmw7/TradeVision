package com.tradevision.service;

import com.tradevision.client.KISApiClient;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.external.KISStockPriceResponse;
import com.tradevision.dto.response.StockPriceResponse;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주식 데이터 서비스
 * 한국투자증권 API를 통한 실시간 주식 시세 조회
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final KISApiClient kisApiClient;

    /**
     * 주식 현재가 조회
     *
     * @param stockCode  종목 코드 (6자리)
     * @param candleType 캔들 타입 (기본값: 일봉)
     * @return 주식 시세 정보
     */
    public StockPriceResponse getStockPrice(String stockCode, CandleType candleType) {
        log.info("주식 시세 조회: {} ({})", stockCode, candleType != null ? candleType.getDisplayName() : "일봉");

        // 캔들 타입 기본값 설정
        CandleType targetCandleType = candleType != null ? candleType : CandleType.D;

        // KIS API 호출
        KISStockPriceResponse kisResponse = kisApiClient.getStockPrice(stockCode, targetCandleType);

        // 응답 데이터 검증
        if (kisResponse.getOutput() == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }

        // DTO 변환
        StockPriceResponse response = convertToStockPriceResponse(stockCode, kisResponse, targetCandleType);

        log.info("주식 시세 조회 완료: {} - 현재가 {}원", stockCode, response.getCurrentPrice());

        return response;
    }

    /**
     * 종목 코드 유효성 검증
     *
     * @param stockCode 종목 코드
     * @return 유효하면 true
     */
    public boolean validateStockCode(String stockCode) {
        if (stockCode == null || !stockCode.matches("^\\d{6}$")) {
            return false;
        }

        try {
            // 실제 API 호출하여 종목 존재 여부 확인
            getStockPrice(stockCode, CandleType.D);
            return true;
        } catch (Exception e) {
            log.warn("종목 코드 검증 실패: {}", stockCode);
            return false;
        }
    }

    /**
     * KIS API 응답을 StockPriceResponse로 변환
     *
     * @param stockCode  종목 코드
     * @param kisResponse KIS API 응답
     * @param candleType 캔들 타입
     * @return StockPriceResponse
     */
    private StockPriceResponse convertToStockPriceResponse(
            String stockCode,
            KISStockPriceResponse kisResponse,
            CandleType candleType) {

        KISStockPriceResponse.Output output = kisResponse.getOutput();

        return StockPriceResponse.builder()
                .stockCode(stockCode)
                .stockName(output.getStockName())
                .currentPrice(parseBigDecimal(output.getCurrentPrice()))
                .priceChange(parseBigDecimal(output.getPriceChange()))
                .changeRate(parseBigDecimal(output.getChangeRate()))
                .volume(parseLong(output.getAccumulatedVolume()))
                .openPrice(parseBigDecimal(output.getOpenPrice()))
                .highPrice(parseBigDecimal(output.getHighPrice()))
                .lowPrice(parseBigDecimal(output.getLowPrice()))
                .previousClose(parseBigDecimal(output.getPreviousClose()))
                .candleType(candleType)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 문자열을 BigDecimal로 안전하게 변환
     *
     * @param value 문자열 값
     * @return BigDecimal (변환 실패 시 0)
     */
    private BigDecimal parseBigDecimal(String value) {
        try {
            return value != null ? new BigDecimal(value.trim()) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            log.warn("BigDecimal 변환 실패: {}", value);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 문자열을 Long으로 안전하게 변환
     *
     * @param value 문자열 값
     * @return Long (변환 실패 시 0)
     */
    private Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value.trim()) : 0L;
        } catch (NumberFormatException e) {
            log.warn("Long 변환 실패: {}", value);
            return 0L;
        }
    }
}
