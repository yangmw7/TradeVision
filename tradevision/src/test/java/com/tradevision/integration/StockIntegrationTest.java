package com.tradevision.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.client.KISApiClient;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.external.KISStockPriceResponse;
import com.tradevision.dto.request.StockSearchRequest;
import com.tradevision.entity.User;
import com.tradevision.repository.RefreshTokenRepository;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 주식 데이터 통합 테스트
 * 실제 Spring Context를 로드하여 전체 주식 조회 플로우 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("주식 데이터 통합 테스트")
class StockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private KISApiClient kisApiClient;

    private KISStockPriceResponse mockKISResponse;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 초기화
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        // Mock KIS API 응답 설정
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

        // Mock KIS API 동작 설정
        given(kisApiClient.getStockPrice(anyString(), any(CandleType.class)))
                .willReturn(mockKISResponse);
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("주식 시세 조회 전체 플로우 테스트 (인증 → 조회)")
    @WithMockUser(username = "test@example.com")
    void fullStockQueryFlow_Success() throws Exception {
        // ========== 주식 시세 조회 (GET) ==========
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.stockName").value("삼성전자"))
                .andExpect(jsonPath("$.data.currentPrice").value(70000))
                .andExpect(jsonPath("$.data.priceChange").value(1000))
                .andExpect(jsonPath("$.data.changeRate").value(1.45))
                .andExpect(jsonPath("$.data.volume").value(5000000))
                .andExpect(jsonPath("$.data.openPrice").value(69000))
                .andExpect(jsonPath("$.data.highPrice").value(71000))
                .andExpect(jsonPath("$.data.lowPrice").value(68000))
                .andExpect(jsonPath("$.data.previousClose").value(69000))
                .andExpect(jsonPath("$.data.candleType").value("D"))
                .andExpect(jsonPath("$.data.timestamp").exists());
    }

    @Test
    @DisplayName("주식 검색 (POST) 전체 플로우 테스트")
    @WithMockUser(username = "test@example.com")
    void stockSearchFlow_Success() throws Exception {
        // given
        StockSearchRequest searchRequest = StockSearchRequest.builder()
                .stockCode("005930")
                .candleType(CandleType.D)
                .build();

        // when & then
        mockMvc.perform(post("/api/stocks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.stockName").value("삼성전자"));
    }

    @Test
    @DisplayName("종목 코드 검증 플로우 테스트")
    @WithMockUser(username = "test@example.com")
    void stockCodeValidationFlow_Success() throws Exception {
        // when & then - 유효한 종목 코드
        mockMvc.perform(get("/api/stocks/validate/005930"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message").value("유효한 종목 코드입니다"));
    }

    @Test
    @DisplayName("다양한 캔들 타입으로 조회 테스트")
    @WithMockUser(username = "test@example.com")
    void differentCandleTypes_Success() throws Exception {
        // 1분봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "M1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("M1"));

        // 5분봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "M5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("M5"));

        // 일봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("D"));

        // 주봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "W"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("W"));

        // 월봉
        mockMvc.perform(get("/api/stocks/005930")
                        .param("candleType", "M"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("M"));
    }

    @Test
    @DisplayName("캔들 타입 없이 조회 시 일봉으로 기본 설정")
    @WithMockUser(username = "test@example.com")
    void noCandleType_DefaultsToDaily() throws Exception {
        // when & then
        mockMvc.perform(get("/api/stocks/005930"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.candleType").value("D"));
    }

    @Test
    @DisplayName("잘못된 종목 코드 형식으로 검색 시 유효성 검증 실패")
    @WithMockUser(username = "test@example.com")
    void invalidStockCodeFormat_ValidationFails() throws Exception {
        // given
        StockSearchRequest invalidRequest = StockSearchRequest.builder()
                .stockCode("12345")  // 5자리 (6자리여야 함)
                .candleType(CandleType.D)
                .build();

        // when & then
        mockMvc.perform(post("/api/stocks/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("인증 없이 주식 조회 시도 - 401 Unauthorized")
    void getStockPrice_NoAuth_Unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/stocks/005930"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("KIS API 응답 데이터가 null인 경우 처리")
    @WithMockUser(username = "test@example.com")
    void kisApiNullResponse_HandledGracefully() throws Exception {
        // given
        KISStockPriceResponse emptyResponse = new KISStockPriceResponse(
                "0", "0000", "성공", null
        );

        given(kisApiClient.getStockPrice(anyString(), any()))
                .willReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/api/stocks/999999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("3005"));
    }
}
