package com.tradevision.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.request.StockSearchRequest;
import com.tradevision.dto.response.StockPriceResponse;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * StockController 단위 테스트
 */
@WebMvcTest(StockController.class)
@DisplayName("StockController 테스트")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockService stockService;

    private StockPriceResponse mockStockPriceResponse;

    @BeforeEach
    void setUp() {
        mockStockPriceResponse = StockPriceResponse.builder()
                .stockCode("005930")
                .stockName("삼성전자")
                .currentPrice(new BigDecimal("70000"))
                .priceChange(new BigDecimal("1000"))
                .changeRate(new BigDecimal("1.45"))
                .volume(5000000L)
                .openPrice(new BigDecimal("69000"))
                .highPrice(new BigDecimal("71000"))
                .lowPrice(new BigDecimal("68000"))
                .previousClose(new BigDecimal("69000"))
                .candleType(CandleType.D)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/stocks/{stockCode} - 주식 시세 조회 성공")
    @WithMockUser
    void getStockPrice_Success() throws Exception {
        // given
        given(stockService.getStockPrice("005930", CandleType.D))
                .willReturn(mockStockPriceResponse);

        // when & then
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("주식 시세 조회에 성공했습니다"))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.stockName").value("삼성전자"))
                .andExpect(jsonPath("$.data.currentPrice").value(70000))
                .andExpect(jsonPath("$.data.changeRate").value(1.45));
    }

    @Test
    @DisplayName("GET /api/stocks/{stockCode} - 캔들 타입 없이 조회")
    @WithMockUser
    void getStockPrice_WithoutCandleType() throws Exception {
        // given
        given(stockService.getStockPrice("005930", null))
                .willReturn(mockStockPriceResponse);

        // when & then
        mockMvc.perform(get("/api/stocks/005930"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockCode").value("005930"));
    }

    @Test
    @DisplayName("GET /api/stocks/{stockCode} - 종목을 찾을 수 없음")
    @WithMockUser
    void getStockPrice_StockNotFound() throws Exception {
        // given
        given(stockService.getStockPrice(anyString(), any()))
                .willThrow(new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/stocks/999999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("3005"));
    }

    @Test
    @DisplayName("GET /api/stocks/{stockCode} - 인증 없이 접근")
    void getStockPrice_Unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/stocks/005930"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/stocks/search - 주식 검색 성공")
    @WithMockUser
    void searchStock_Success() throws Exception {
        // given
        StockSearchRequest request = StockSearchRequest.builder()
                .stockCode("005930")
                .candleType(CandleType.D)
                .build();

        given(stockService.getStockPrice("005930", CandleType.D))
                .willReturn(mockStockPriceResponse);

        // when & then
        mockMvc.perform(post("/api/stocks/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.stockName").value("삼성전자"));
    }

    @Test
    @DisplayName("POST /api/stocks/search - 유효성 검증 실패 (잘못된 종목 코드)")
    @WithMockUser
    void searchStock_InvalidStockCode() throws Exception {
        // given
        StockSearchRequest request = StockSearchRequest.builder()
                .stockCode("12345")  // 5자리
                .candleType(CandleType.D)
                .build();

        // when & then
        mockMvc.perform(post("/api/stocks/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/stocks/search - 유효성 검증 실패 (종목 코드 누락)")
    @WithMockUser
    void searchStock_MissingStockCode() throws Exception {
        // given
        StockSearchRequest request = StockSearchRequest.builder()
                .candleType(CandleType.D)
                .build();

        // when & then
        mockMvc.perform(post("/api/stocks/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/stocks/validate/{stockCode} - 유효한 종목 코드")
    @WithMockUser
    void validateStockCode_Valid() throws Exception {
        // given
        given(stockService.validateStockCode("005930"))
                .willReturn(true);

        // when & then
        mockMvc.perform(get("/api/stocks/validate/005930"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유효한 종목 코드입니다"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET /api/stocks/validate/{stockCode} - 유효하지 않은 종목 코드")
    @WithMockUser
    void validateStockCode_Invalid() throws Exception {
        // given
        given(stockService.validateStockCode("999999"))
                .willReturn(false);

        // when & then
        mockMvc.perform(get("/api/stocks/validate/999999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유효하지 않은 종목 코드입니다"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("다양한 캔들 타입으로 조회")
    @WithMockUser
    void getStockPrice_DifferentCandleTypes() throws Exception {
        // given
        given(stockService.getStockPrice(anyString(), any()))
                .willReturn(mockStockPriceResponse);

        // when & then - 1분봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "M1"))
                .andDo(print())
                .andExpect(status().isOk());

        // when & then - 5분봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "M5"))
                .andDo(print())
                .andExpect(status().isOk());

        // when & then - 주봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "W"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
