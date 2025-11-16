package com.tradevision.client;

import com.tradevision.constant.CandleType;
import com.tradevision.dto.external.KISStockPriceResponse;
import com.tradevision.dto.external.KISTokenResponse;
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
import java.time.LocalDateTime;

/**
 * 한국투자증권 OpenAPI 클라이언트
 * WebClient를 사용한 비동기 HTTP 통신
 */
@Component
@RequiredArgsConstructor
public class KISApiClient {

    private static final Logger log = LoggerFactory.getLogger(KISApiClient.class);

    private final WebClient webClient;

    @Value("${external-api.kis.base-url}")
    private String baseUrl;

    @Value("${external-api.kis.app-key}")
    private String appKey;

    @Value("${external-api.kis.app-secret}")
    private String appSecret;

    // 토큰 캐싱을 위한 변수
    private String cachedAccessToken;
    private LocalDateTime tokenExpireTime;

    /**
     * OAuth 토큰 발급
     * 토큰은 24시간 유효하므로 캐싱하여 재사용
     *
     * @return Access Token
     */
    @CircuitBreaker(name = "kisApi", fallbackMethod = "getTokenFallback")
    @Retry(name = "kisApi")
    public String getAccessToken() {
        // 캐시된 토큰이 있고 만료되지 않았으면 재사용
        if (cachedAccessToken != null && tokenExpireTime != null
                && LocalDateTime.now().isBefore(tokenExpireTime)) {
            log.debug("캐시된 KIS Access Token 사용");
            return cachedAccessToken;
        }

        log.info("KIS API Access Token 발급 요청");

        try {
            String requestBody = String.format(
                    "{\"grant_type\":\"client_credentials\",\"appkey\":\"%s\",\"appsecret\":\"%s\"}",
                    appKey, appSecret
            );

            KISTokenResponse response = webClient.post()
                    .uri(baseUrl + "/oauth2/tokenP")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(KISTokenResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || response.getAccessToken() == null) {
                throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR, "토큰 발급 실패");
            }

            // 토큰 캐싱 (만료 시간 - 10분 여유)
            cachedAccessToken = response.getAccessToken();
            tokenExpireTime = LocalDateTime.now().plusSeconds(response.getExpiresIn() - 600);

            log.info("KIS Access Token 발급 성공");
            return cachedAccessToken;

        } catch (WebClientResponseException e) {
            log.error("KIS API 토큰 발급 실패: {}", e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                    "KIS API 토큰 발급 실패: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("KIS API 통신 오류: {}", e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                    "KIS API 통신 오류: " + e.getMessage());
        }
    }

    /**
     * 주식 현재가 조회
     *
     * @param stockCode  종목 코드 (6자리)
     * @param candleType 캔들 타입
     * @return 주식 시세 정보
     */
    @CircuitBreaker(name = "kisApi", fallbackMethod = "getStockPriceFallback")
    @Retry(name = "kisApi")
    public KISStockPriceResponse getStockPrice(String stockCode, CandleType candleType) {
        log.info("KIS API 주식 시세 조회: {} ({})", stockCode, candleType.getDisplayName());

        String accessToken = getAccessToken();

        try {
            String trId = "FHKST01010100"; // 주식현재가 시세 조회 TR ID

            KISStockPriceResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(baseUrl + "/uapi/domestic-stock/v1/quotations/inquire-price")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J") // 시장 구분 (J: 주식)
                            .queryParam("FID_INPUT_ISCD", stockCode) // 종목 코드
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", trId)
                    .retrieve()
                    .bodyToMono(KISStockPriceResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || !"0".equals(response.getResultCode())) {
                throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                        "주식 시세 조회 실패: " + (response != null ? response.getMessage() : "응답 없음"));
            }

            log.info("KIS API 주식 시세 조회 성공: {}", stockCode);
            return response;

        } catch (WebClientResponseException e) {
            log.error("KIS API 주식 시세 조회 실패: {}", e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                    "주식 시세 조회 실패: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("KIS API 통신 오류: {}", e.getMessage());
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR,
                    "KIS API 통신 오류: " + e.getMessage());
        }
    }

    /**
     * Circuit Breaker Fallback - 토큰 발급 실패 시
     */
    private String getTokenFallback(Exception e) {
        log.error("KIS API Circuit Breaker 작동 (토큰 발급): {}", e.getMessage());
        throw new ExternalApiException(ErrorCode.EXTERNAL_API_UNAVAILABLE,
                "한국투자증권 API 서버에 일시적으로 접근할 수 없습니다");
    }

    /**
     * Circuit Breaker Fallback - 주식 시세 조회 실패 시
     */
    private KISStockPriceResponse getStockPriceFallback(String stockCode, CandleType candleType, Exception e) {
        log.error("KIS API Circuit Breaker 작동 (시세 조회): {}", e.getMessage());
        throw new ExternalApiException(ErrorCode.EXTERNAL_API_UNAVAILABLE,
                "한국투자증권 API 서버에 일시적으로 접근할 수 없습니다");
    }
}
