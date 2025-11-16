package com.tradevision.client;

import com.tradevision.dto.external.OpenAIRequest;
import com.tradevision.dto.external.OpenAIResponse;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ExternalApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * OpenAI API 클라이언트
 * GPT-4 Vision을 사용한 차트 이미지 분석
 */
@Component
@RequiredArgsConstructor
public class OpenAIClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAIClient.class);
    private static final String CHAT_COMPLETIONS_ENDPOINT = "/v1/chat/completions";

    private final WebClient webClient;

    @Value("${external-api.openai.base-url}")
    private String baseUrl;

    @Value("${external-api.openai.api-key}")
    private String apiKey;

    @Value("${external-api.openai.model:gpt-4o}")
    private String model;

    @Value("${external-api.openai.max-tokens:2000}")
    private Integer maxTokens;

    /**
     * GPT-4 Vision을 사용한 차트 이미지 분석
     *
     * @param base64Image Base64로 인코딩된 차트 이미지
     * @param prompt      분석 요청 프롬프트
     * @return AI 분석 결과
     */
    @CircuitBreaker(name = "openaiApi", fallbackMethod = "analyzeChartFallback")
    @Retry(name = "openaiApi")
    public String analyzeChart(String base64Image, String prompt) {
        log.info("OpenAI API 차트 분석 요청 시작");

        try {
            // OpenAI API 요청 생성
            OpenAIRequest request = buildAnalysisRequest(base64Image, prompt);

            // API 호출
            OpenAIResponse response = webClient.post()
                    .uri(baseUrl + CHAT_COMPLETIONS_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .timeout(Duration.ofSeconds(60))  // 60초 타임아웃
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new ExternalApiException(ErrorCode.OPENAI_API_ERROR, "OpenAI API 응답이 비어있습니다");
            }

            String analysisResult = response.getChoices().get(0).getMessage().getContent();

            if (analysisResult == null || analysisResult.isBlank()) {
                throw new ExternalApiException(ErrorCode.OPENAI_API_ERROR, "AI 분석 결과가 비어있습니다");
            }

            log.info("OpenAI API 차트 분석 완료 (토큰 사용: {})",
                    response.getUsage() != null ? response.getUsage().getTotalTokens() : "N/A");

            return analysisResult;

        } catch (WebClientResponseException e) {
            log.error("OpenAI API 호출 실패: {} - {}", e.getStatusCode(), e.getMessage());
            throw new ExternalApiException(ErrorCode.OPENAI_API_ERROR,
                    "OpenAI API 호출 실패: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("OpenAI API 통신 오류: {}", e.getMessage(), e);
            throw new ExternalApiException(ErrorCode.OPENAI_API_ERROR,
                    "AI 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * OpenAI API 요청 생성
     *
     * @param base64Image Base64 인코딩된 이미지
     * @param prompt      분석 프롬프트
     * @return OpenAI API 요청 객체
     */
    private OpenAIRequest buildAnalysisRequest(String base64Image, String prompt) {
        // 이미지 URL 객체 생성 (Base64 데이터 URL 형식)
        OpenAIRequest.ImageUrl imageUrl = OpenAIRequest.ImageUrl.builder()
                .url("data:image/jpeg;base64," + base64Image)
                .detail("high")  // 고해상도 분석
                .build();

        // 콘텐츠 목록 생성 (텍스트 + 이미지)
        List<OpenAIRequest.Content> contents = List.of(
                OpenAIRequest.Content.builder()
                        .type("text")
                        .text(prompt)
                        .build(),
                OpenAIRequest.Content.builder()
                        .type("image_url")
                        .imageUrl(imageUrl)
                        .build()
        );

        // 메시지 생성
        OpenAIRequest.Message message = OpenAIRequest.Message.builder()
                .role("user")
                .content(contents)
                .build();

        // 요청 객체 생성
        return OpenAIRequest.builder()
                .model(model)
                .messages(List.of(message))
                .maxTokens(maxTokens)
                .build();
    }

    /**
     * Circuit Breaker Fallback - AI 분석 실패 시
     */
    private String analyzeChartFallback(String base64Image, String prompt, Exception e) {
        log.error("OpenAI API Circuit Breaker 작동: {}", e.getMessage());
        throw new ExternalApiException(ErrorCode.EXTERNAL_API_UNAVAILABLE,
                "AI 분석 서비스에 일시적으로 접근할 수 없습니다. 잠시 후 다시 시도해주세요");
    }

    /**
     * 차트 분석용 프롬프트 생성
     *
     * @param stockCode  종목 코드
     * @param stockName  종목명
     * @param candleType 캔들 타입
     * @return 분석 프롬프트
     */
    public String buildChartAnalysisPrompt(String stockCode, String stockName, String candleType) {
        return String.format("""
                당신은 전문 주식 차트 분석가입니다. 제공된 주식 차트 이미지를 분석하여 다음 정보를 제공해주세요:

                종목 정보:
                - 종목 코드: %s
                - 종목명: %s
                - 캔들 타입: %s

                다음 형식의 JSON으로 응답해주세요:
                {
                  "pattern": "감지된 차트 패턴 (예: 상승 삼각형, 헤드앤숄더, 쌍바닥 등)",
                  "trend": "현재 추세 (상승/하락/횡보)",
                  "supportLevel": "주요 지지선 가격",
                  "resistanceLevel": "주요 저항선 가격",
                  "volumeAnalysis": "거래량 분석 (증가/감소 추세)",
                  "tradingOpinion": "매매 의견 (매수/매도/관망)",
                  "summary": "종합 분석 의견 (2-3문장)",
                  "keyPoints": ["주요 포인트 1", "주요 포인트 2", "주요 포인트 3"],
                  "riskLevel": "위험도 (낮음/보통/높음)"
                }

                주의사항:
                - 반드시 JSON 형식으로만 응답하세요
                - 모든 분석은 한국어로 작성하세요
                - 객관적이고 전문적인 분석을 제공하세요
                - 투자 권유가 아닌 기술적 분석에 집중하세요
                """,
                stockCode != null ? stockCode : "미제공",
                stockName != null ? stockName : "미제공",
                candleType != null ? candleType : "미제공"
        );
    }
}
