package com.tradevision.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.client.OpenAIClient;
import com.tradevision.constant.CandleType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.entity.User;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChartAnalysis 통합 테스트
 * 전체 애플리케이션 컨텍스트에서 차트 분석 기능을 종단간 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ChartAnalysis 통합 테스트")
class ChartAnalysisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpenAIClient openAIClient;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .nickname("테스터")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        userRepository.save(testUser);

        // OpenAI Client 모킹
        String mockAnalysisJson = """
                {
                  "pattern": "상승 삼각형",
                  "trend": "상승",
                  "supportLevel": "68000",
                  "resistanceLevel": "72000",
                  "tradingOpinion": "매수",
                  "summary": "강한 상승 추세",
                  "riskLevel": "보통"
                }
                """;

        given(openAIClient.buildChartAnalysisPrompt(anyString(), anyString(), anyString()))
                .willReturn("Mock prompt");
        given(openAIClient.analyzeChart(anyString(), anyString()))
                .willReturn(mockAnalysisJson);
    }

    @Test
    @DisplayName("차트 분석 전체 플로우 - 업로드부터 조회까지")
    @WithMockUser(username = "test@example.com")
    void chartAnalysisFullFlow_Success() throws Exception {
        // 1. 차트 이미지 업로드 및 분석
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "samsung.jpg",
                "image/jpeg",
                "mock chart image content".getBytes()
        );

        // Note: 실제 OpenAI API 호출이 mock되어야 함
        // 통합 테스트에서는 @MockBean을 사용하거나 테스트 프로파일에서 mock 설정 필요
        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("stockCode", "005930")
                        .param("stockName", "삼성전자")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("차트 분석이 완료되었습니다"))
                .andExpect(jsonPath("$.data.stockCode").value("005930"))
                .andExpect(jsonPath("$.data.stockName").value("삼성전자"))
                .andExpect(jsonPath("$.data.candleType").value("D"));

        // 2. 분석 히스토리 조회
        mockMvc.perform(get("/api/chart-analysis/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("여러 차트 분석 후 히스토리 페이징 검증")
    @WithMockUser(username = "test@example.com")
    void multipleAnalyses_PaginationWorks() throws Exception {
        // 여러 차트 이미지 업로드
        for (int i = 0; i < 3; i++) {
            MockMultipartFile chartImage = new MockMultipartFile(
                    "chartImage",
                    "chart" + i + ".jpg",
                    "image/jpeg",
                    ("mock chart image " + i).getBytes()
            );

            mockMvc.perform(multipart("/api/chart-analysis")
                            .file(chartImage)
                            .param("stockCode", "00593" + i)
                            .param("stockName", "테스트종목" + i)
                            .param("candleType", "D"))
                    .andExpect(status().isOk());
        }

        // 첫 번째 페이지 조회 (2개씩)
        mockMvc.perform(get("/api/chart-analysis/history")
                        .param("page", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.data.totalPages", greaterThanOrEqualTo(2)));

        // 두 번째 페이지 조회
        mockMvc.perform(get("/api/chart-analysis/history")
                        .param("page", "1")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("일일 분석 한도 초과 검증")
    @WithMockUser(username = "test@example.com")
    void dailyLimitExceeded_ThrowsException() throws Exception {
        // 설정된 일일 한도(10)까지 분석 수행
        for (int i = 0; i < 10; i++) {
            MockMultipartFile chartImage = new MockMultipartFile(
                    "chartImage",
                    "chart" + i + ".jpg",
                    "image/jpeg",
                    ("mock chart " + i).getBytes()
            );

            mockMvc.perform(multipart("/api/chart-analysis")
                            .file(chartImage)
                            .param("stockCode", "00593" + i)
                            .param("candleType", "D"))
                    .andExpect(status().isOk());
        }

        // 한도 초과 시도
        MockMultipartFile extraImage = new MockMultipartFile(
                "chartImage",
                "extra.jpg",
                "image/jpeg",
                "extra image".getBytes()
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(extraImage)
                        .param("stockCode", "005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4007"));
    }

    @Test
    @DisplayName("이미지 크기 초과 - 5MB 제한")
    @WithMockUser(username = "test@example.com")
    void imageSizeExceeded_ThrowsException() throws Exception {
        // 6MB 크기의 이미지 생성
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile largeImage = new MockMultipartFile(
                "chartImage",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(largeImage)
                        .param("stockCode", "005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4002"));
    }

    @Test
    @DisplayName("잘못된 이미지 형식 - TXT 파일")
    @WithMockUser(username = "test@example.com")
    void invalidImageFormat_ThrowsException() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
                "chartImage",
                "chart.txt",
                "text/plain",
                "not an image".getBytes()
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(textFile)
                        .param("stockCode", "005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("4001"));
    }

    @Test
    @DisplayName("PNG 이미지 형식 지원")
    @WithMockUser(username = "test@example.com")
    void pngImageFormat_Success() throws Exception {
        MockMultipartFile pngImage = new MockMultipartFile(
                "chartImage",
                "chart.png",
                "image/png",
                "png image content".getBytes()
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(pngImage)
                        .param("stockCode", "005930")
                        .param("stockName", "삼성전자")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("인증 없이 차트 분석 시도 - 401 Unauthorized")
    void analyzeWithoutAuth_Unauthorized() throws Exception {
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("stockCode", "005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("분석 상세 조회 - 존재하는 ID")
    @WithMockUser(username = "test@example.com")
    void getAnalysisById_Success() throws Exception {
        // 1. 먼저 분석 생성
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        String createResponse = mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("stockCode", "005930")
                        .param("stockName", "삼성전자")
                        .param("candleType", "D"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. 생성된 분석 ID 추출
        Long analysisId = objectMapper.readTree(createResponse)
                .get("data")
                .get("analysisId")
                .asLong();

        // 3. 상세 조회
        mockMvc.perform(get("/api/chart-analysis/" + analysisId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analysisId").value(analysisId))
                .andExpect(jsonPath("$.data.stockCode").value("005930"));
    }

    @Test
    @DisplayName("분석 상세 조회 - 존재하지 않는 ID")
    @WithMockUser(username = "test@example.com")
    void getAnalysisById_NotFound() throws Exception {
        mockMvc.perform(get("/api/chart-analysis/999999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("2004"));
    }

    @Test
    @DisplayName("다양한 캔들 타입 지원 검증")
    @WithMockUser(username = "test@example.com")
    void differentCandleTypes_AllSupported() throws Exception {
        String[] candleTypes = {"D", "W", "M"};

        for (String candleType : candleTypes) {
            MockMultipartFile chartImage = new MockMultipartFile(
                    "chartImage",
                    "chart_" + candleType + ".jpg",
                    "image/jpeg",
                    ("chart " + candleType).getBytes()
            );

            mockMvc.perform(multipart("/api/chart-analysis")
                            .file(chartImage)
                            .param("stockCode", "005930")
                            .param("stockName", "삼성전자")
                            .param("candleType", candleType))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.candleType").value(candleType));
        }
    }

    @Test
    @DisplayName("빈 이미지 파일 업로드 - 유효성 검증 실패")
    @WithMockUser(username = "test@example.com")
    void emptyImageFile_ValidationFails() throws Exception {
        MockMultipartFile emptyImage = new MockMultipartFile(
                "chartImage",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(emptyImage)
                        .param("stockCode", "005930")
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("필수 파라미터 누락 - stockCode 없음")
    @WithMockUser(username = "test@example.com")
    void missingRequiredParameter_StockCode() throws Exception {
        MockMultipartFile chartImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        // stockCode 파라미터 없이 요청
        mockMvc.perform(multipart("/api/chart-analysis")
                        .file(chartImage)
                        .param("candleType", "D"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
