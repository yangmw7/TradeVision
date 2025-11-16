package com.tradevision.service;

import com.tradevision.client.KISApiClient;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.external.KISStockPriceResponse;
import com.tradevision.dto.response.StockPriceResponse;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * StockService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StockService 테스트")
class StockServiceTest {

    @Mock
    private KISApiClient kisApiClient;

    @InjectMocks
    private StockService stockService;

    private KISStockPriceResponse mockKISResponse;

    @BeforeEach
    void setUp() {
        KISStockPriceResponse.Output output = new KISStockPriceResponse.Output(
                "70000",      // 현재가
                "1000",       // 전일 대비
                "1.45",       // 변동률
                "5000000",    // 거래량
                "69000",      // 시가
                "71000",      // 고가
                "68000",      // 저가
                "69000",      // 전일 종가
                "삼성전자"     // 종목명
        );

        mockKISResponse = new KISStockPriceResponse(
                "0",
                "0000",
                "성공",
                output
        );
    }

    @Test
    @DisplayName("주식 시세 조회 성공")
    void getStockPrice_Success() {
        // given
        String stockCode = "005930";
        CandleType candleType = CandleType.D;

        given(kisApiClient.getStockPrice(stockCode, candleType))
                .willReturn(mockKISResponse);

        // when
        StockPriceResponse result = stockService.getStockPrice(stockCode, candleType);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStockCode()).isEqualTo("005930");
        assertThat(result.getStockName()).isEqualTo("삼성전자");
        assertThat(result.getCurrentPrice()).isEqualTo(new BigDecimal("70000"));
        assertThat(result.getPriceChange()).isEqualTo(new BigDecimal("1000"));
        assertThat(result.getChangeRate()).isEqualTo(new BigDecimal("1.45"));
        assertThat(result.getVolume()).isEqualTo(5000000L);
        assertThat(result.getOpenPrice()).isEqualTo(new BigDecimal("69000"));
        assertThat(result.getHighPrice()).isEqualTo(new BigDecimal("71000"));
        assertThat(result.getLowPrice()).isEqualTo(new BigDecimal("68000"));
        assertThat(result.getPreviousClose()).isEqualTo(new BigDecimal("69000"));
        assertThat(result.getCandleType()).isEqualTo(CandleType.D);
        assertThat(result.getTimestamp()).isNotNull();

        verify(kisApiClient).getStockPrice(stockCode, candleType);
    }

    @Test
    @DisplayName("주식 시세 조회 - 캔들 타입 null이면 일봉으로 기본 설정")
    void getStockPrice_NullCandleType_DefaultsToDaily() {
        // given
        String stockCode = "005930";

        given(kisApiClient.getStockPrice(stockCode, CandleType.D))
                .willReturn(mockKISResponse);

        // when
        StockPriceResponse result = stockService.getStockPrice(stockCode, null);

        // then
        assertThat(result.getCandleType()).isEqualTo(CandleType.D);
        verify(kisApiClient).getStockPrice(stockCode, CandleType.D);
    }

    @Test
    @DisplayName("주식 시세 조회 실패 - null output")
    void getStockPrice_NullOutput_ThrowsException() {
        // given
        String stockCode = "999999";
        CandleType candleType = CandleType.D;

        KISStockPriceResponse emptyResponse = new KISStockPriceResponse(
                "0", "0000", "성공", null
        );

        given(kisApiClient.getStockPrice(stockCode, candleType))
                .willReturn(emptyResponse);

        // when & then
        assertThatThrownBy(() -> stockService.getStockPrice(stockCode, candleType))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STOCK_NOT_FOUND);
    }

    @Test
    @DisplayName("종목 코드 유효성 검증 성공")
    void validateStockCode_ValidCode_ReturnsTrue() {
        // given
        String validStockCode = "005930";

        given(kisApiClient.getStockPrice(validStockCode, CandleType.D))
                .willReturn(mockKISResponse);

        // when
        boolean result = stockService.validateStockCode(validStockCode);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("종목 코드 유효성 검증 실패 - 잘못된 형식")
    void validateStockCode_InvalidFormat_ReturnsFalse() {
        // given
        String invalidStockCode = "12345"; // 5자리

        // when
        boolean result = stockService.validateStockCode(invalidStockCode);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("종목 코드 유효성 검증 실패 - null")
    void validateStockCode_Null_ReturnsFalse() {
        // when
        boolean result = stockService.validateStockCode(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("종목 코드 유효성 검증 실패 - 영문자 포함")
    void validateStockCode_ContainsLetters_ReturnsFalse() {
        // given
        String invalidStockCode = "00593A";

        // when
        boolean result = stockService.validateStockCode(invalidStockCode);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("종목 코드 유효성 검증 실패 - API 호출 실패")
    void validateStockCode_ApiCallFails_ReturnsFalse() {
        // given
        String stockCode = "999999";

        given(kisApiClient.getStockPrice(stockCode, CandleType.D))
                .willThrow(new RuntimeException("API 오류"));

        // when
        boolean result = stockService.validateStockCode(stockCode);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("BigDecimal 변환 - 빈 문자열")
    void parseBigDecimal_EmptyString() {
        // given
        KISStockPriceResponse.Output output = new KISStockPriceResponse.Output(
                "",  // 빈 문자열
                "1000", "1.45", "5000000",
                "69000", "71000", "68000", "69000", "테스트"
        );

        KISStockPriceResponse response = new KISStockPriceResponse(
                "0", "0000", "성공", output
        );

        given(kisApiClient.getStockPrice(anyString(), any()))
                .willReturn(response);

        // when
        StockPriceResponse result = stockService.getStockPrice("005930", CandleType.D);

        // then
        assertThat(result.getCurrentPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Long 변환 - null 값")
    void parseLong_NullValue() {
        // given
        KISStockPriceResponse.Output output = new KISStockPriceResponse.Output(
                "70000", "1000", "1.45",
                null,  // null 거래량
                "69000", "71000", "68000", "69000", "테스트"
        );

        KISStockPriceResponse response = new KISStockPriceResponse(
                "0", "0000", "성공", output
        );

        given(kisApiClient.getStockPrice(anyString(), any()))
                .willReturn(response);

        // when
        StockPriceResponse result = stockService.getStockPrice("005930", CandleType.D);

        // then
        assertThat(result.getVolume()).isEqualTo(0L);
    }
}
