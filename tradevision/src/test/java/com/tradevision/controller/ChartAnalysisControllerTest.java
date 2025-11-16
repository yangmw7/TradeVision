package com.tradevision.controller;

import com.tradevision.constant.CandleType;
import com.tradevision.dto.response.ChartAnalysisResponse;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.service.ChartAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChartAnalysisController 단위 테스트
 */
@WebMvcTest(ChartAnalysisController.class)
@DisplayName("ChartAnalysisController 테스트")
class ChartAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChartAnalysisService chartAnalysisService;

    @MockBean
    private com.tradevision.client.OpenAIClient openAIClient;

    @MockBean
    private com.tradevision.repository.UserRepository userRepository;

    @MockBean
    private com.tradevision.repository.ChartAnalysisRepository chartAnalysisRepository;

    @MockBean
    private com.tradevision.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.tradevision.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    private ChartAnalysisResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = ChartAnalysisResponse.builder()
                .analysisId(1L)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .imagePath("uploads/charts/1/test.jpg")
                .analysisResult(ChartAnalysisResponse.AnalysisResult.builder()
                        .pattern("상승 삼각형")
                        .trend("상승")
                        .supportLevel("68000")
                        .resistanceLevel("72000")
                        .tradingOpinion("매수")
                        .summary("강한 상승 추세")
                        .riskLevel("보통")
                        .build())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/chart-analysis - 차트 분석 성공")
    @WithMockUser
    void analyzeChart_Success() throws Exception {
        // given
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        given(chartAnalysisService.analyzeChart(any(), anyLong()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("stockCode", "005930")
                        .param("stockName", "삼성전자")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("차트 분석이 완료되었습니다"))
                .andExpect(jsonPath("$.data.analysisId").value(1))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.analysisResult.pattern").value("상승 삼각형"));
    }

    @Test
    @DisplayName("POST /api/chart-analysis - 일일 한도 초과")
    @WithMockUser
    void analyzeChart_DailyLimitExceeded() throws Exception {
        // given
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        given(chartAnalysisService.analyzeChart(any(), anyLong()))
                .willThrow(new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED));

        // when & then
        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4007"));
    }

    @Test
    @DisplayName("GET /api/chart-analysis/history - 히스토리 조회 성공")
    @WithMockUser
    void getAnalysisHistory_Success() throws Exception {
        // given
        Page<ChartAnalysisResponse> historyPage = new PageImpl<>(List.of(mockResponse));

        given(chartAnalysisService.getAnalysisHistory(anyLong(), any(Pageable.class)))
                .willReturn(historyPage);

        // when & then
        mockMvc.perform(get("/api/chart-analysis/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].analysisId").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/chart-analysis/{analysisId} - 상세 조회 성공")
    @WithMockUser
    void getAnalysisById_Success() throws Exception {
        // given
        given(chartAnalysisService.getAnalysisById(1L, anyLong()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/chart-analysis/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analysisId").value(1))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.analysisResult.pattern").value("상승 삼각형"));
    }

    @Test
    @DisplayName("GET /api/chart-analysis/{analysisId} - 분석 결과 없음")
    @WithMockUser
    void getAnalysisById_NotFound() throws Exception {
        // given
        given(chartAnalysisService.getAnalysisById(999L, anyLong()))
                .willThrow(new ResourceNotFoundException(ErrorCode.ANALYSIS_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/chart-analysis/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("2004"));
    }

    @Test
    @DisplayName("POST /api/chart-analysis - 인증 없이 접근")
    void analyzeChart_Unauthorized() throws Exception {
        // given
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/chart-analysis - 이미지 파일 없음")
    @WithMockUser
    void analyzeChart_NoImage() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/chart-analysis")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
