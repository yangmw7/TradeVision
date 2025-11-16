package com.tradevision.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.client.OpenAIClient;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.request.ChartAnalysisRequest;
import com.tradevision.dto.response.ChartAnalysisResponse;
import com.tradevision.entity.ChartAnalysis;
import com.tradevision.entity.User;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.repository.ChartAnalysisRepository;
import com.tradevision.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * ChartAnalysisService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChartAnalysisService 테스트")
class ChartAnalysisServiceTest {

    @Mock
    private ChartAnalysisRepository chartAnalysisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChartAnalysisService chartAnalysisService;

    private User testUser;
    private ChartAnalysis testAnalysis;
    private MultipartFile validImage;
    private String mockAnalysisJson;

    @BeforeEach
    void setUp() {
        // 필드 설정
        ReflectionTestUtils.setField(chartAnalysisService, "uploadDir", "uploads/charts");
        ReflectionTestUtils.setField(chartAnalysisService, "dailyAnalysisLimit", 10);

        // 테스트 사용자
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스터")
                .build();

        // 유효한 이미지 파일
        validImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Mock AI 분석 결과 JSON
        mockAnalysisJson = """
                {
                  "pattern": "상승 삼각형",
                  "trend": "상승",
                  "supportLevel": "68000",
                  "resistanceLevel": "72000",
                  "volumeAnalysis": "거래량 증가",
                  "tradingOpinion": "매수",
                  "summary": "강한 상승 추세",
                  "keyPoints": ["지지선 확보", "거래량 급증"],
                  "riskLevel": "보통"
                }
                """;

        // 테스트 분석 결과
        testAnalysis = ChartAnalysis.builder()
                .id(1L)
                .user(testUser)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .imagePath("uploads/charts/1/test.jpg")
                .analysisResult(mockAnalysisJson)
                .build();
    }

    @Test
    @DisplayName("차트 분석 성공")
    void analyzeChart_Success() throws Exception {
        // given
        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(validImage)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(0L);
        given(openAIClient.buildChartAnalysisPrompt(anyString(), anyString(), anyString()))
                .willReturn("분석 프롬프트");
        given(openAIClient.analyzeChart(anyString(), anyString()))
                .willReturn(mockAnalysisJson);
        given(chartAnalysisRepository.save(any(ChartAnalysis.class)))
                .willReturn(testAnalysis);
        given(objectMapper.readValue(anyString(), eq(ChartAnalysisResponse.AnalysisResult.class)))
                .willReturn(ChartAnalysisResponse.AnalysisResult.builder()
                        .pattern("상승 삼각형")
                        .trend("상승")
                        .build());

        // when
        ChartAnalysisResponse result = chartAnalysisService.analyzeChart(request, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAnalysisId()).isEqualTo(1L);
        assertThat(result.getStockCode()).isEqualTo("005930");

        verify(userRepository).findById(1L);
        verify(chartAnalysisRepository).save(any(ChartAnalysis.class));
    }

    @Test
    @DisplayName("차트 분석 실패 - 사용자 없음")
    void analyzeChart_UserNotFound_ThrowsException() {
        // given
        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(validImage)
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.analyzeChart(request, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("차트 분석 실패 - 일일 한도 초과")
    void analyzeChart_DailyLimitExceeded_ThrowsException() {
        // given
        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(validImage)
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(10L);  // 한도 도달

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.analyzeChart(request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DAILY_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("차트 분석 실패 - 이미지 파일 크기 초과")
    void analyzeChart_ImageSizeExceeded_ThrowsException() {
        // given
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MultipartFile largeImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                largeContent
        );

        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(largeImage)
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(0L);

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.analyzeChart(request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.IMAGE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("차트 분석 실패 - 잘못된 이미지 형식")
    void analyzeChart_InvalidImageFormat_ThrowsException() {
        // given
        MultipartFile invalidImage = new MockMultipartFile(
                "chartImage",
                "chart.txt",  // 텍스트 파일
                "text/plain",
                "test content".getBytes()
        );

        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(invalidImage)
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(0L);

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.analyzeChart(request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_IMAGE_FORMAT);
    }

    @Test
    @DisplayName("분석 히스토리 조회 성공")
    void getAnalysisHistory_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChartAnalysis> analysisPage = new PageImpl<>(List.of(testAnalysis));

        given(chartAnalysisRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable))
                .willReturn(analysisPage);

        // when
        Page<ChartAnalysisResponse> result = chartAnalysisService.getAnalysisHistory(1L, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockCode()).isEqualTo("005930");

        verify(chartAnalysisRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("분석 상세 조회 성공")
    void getAnalysisById_Success() {
        // given
        given(chartAnalysisRepository.findByIdAndUserId(1L, 1L))
                .willReturn(Optional.of(testAnalysis));

        // when
        ChartAnalysisResponse result = chartAnalysisService.getAnalysisById(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAnalysisId()).isEqualTo(1L);
        assertThat(result.getStockCode()).isEqualTo("005930");

        verify(chartAnalysisRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("분석 상세 조회 실패 - 분석 결과 없음")
    void getAnalysisById_NotFound_ThrowsException() {
        // given
        given(chartAnalysisRepository.findByIdAndUserId(999L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.getAnalysisById(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ANALYSIS_NOT_FOUND);
    }

    @Test
    @DisplayName("PNG 이미지 파일 허용")
    void analyzeChart_PngImage_Success() throws Exception {
        // given
        MultipartFile pngImage = new MockMultipartFile(
                "chartImage",
                "chart.png",
                "image/png",
                "png image content".getBytes()
        );

        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(pngImage)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(0L);
        given(openAIClient.buildChartAnalysisPrompt(anyString(), anyString(), anyString()))
                .willReturn("프롬프트");
        given(openAIClient.analyzeChart(anyString(), anyString()))
                .willReturn(mockAnalysisJson);
        given(chartAnalysisRepository.save(any(ChartAnalysis.class)))
                .willReturn(testAnalysis);
        given(objectMapper.readValue(anyString(), eq(ChartAnalysisResponse.AnalysisResult.class)))
                .willReturn(ChartAnalysisResponse.AnalysisResult.builder()
                        .pattern("상승 삼각형")
                        .trend("상승")
                        .build());

        // when
        ChartAnalysisResponse result = chartAnalysisService.analyzeChart(request, 1L);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("빈 이미지 파일 - 유효성 검증 실패")
    void analyzeChart_EmptyImage_ThrowsException() {
        // given
        MultipartFile emptyImage = new MockMultipartFile(
                "chartImage",
                "chart.jpg",
                "image/jpeg",
                new byte[0]  // 빈 파일
        );

        ChartAnalysisRequest request = ChartAnalysisRequest.builder()
                .chartImage(emptyImage)
                .candleType(CandleType.D)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(chartAnalysisRepository.countTodayAnalyses(anyLong(), any(LocalDateTime.class)))
                .willReturn(0L);

        // when & then
        assertThatThrownBy(() -> chartAnalysisService.analyzeChart(request, 1L))
                .isInstanceOf(BusinessException.class);
    }
}
