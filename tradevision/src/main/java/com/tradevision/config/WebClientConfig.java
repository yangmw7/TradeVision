package com.tradevision.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 설정
 * 외부 API 호출을 위한 비동기 HTTP 클라이언트
 */
@Configuration
public class WebClientConfig {

    @Value("${external-api.kis.base-url:https://openapivts.koreainvestment.com:29443}")
    private String kisBaseUrl;

    @Value("${external-api.openai.timeout:30000}")
    private int openaiTimeout;

    /**
     * 기본 WebClient 빌더 (전역 설정)
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        // Netty HttpClient 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 10초
                .responseTimeout(Duration.ofSeconds(30)) // 응답 타임아웃 30초
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    /**
     * 기본 WebClient 인스턴스
     * KISApiClient, OpenAIClient 등에서 사용
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    /**
     * KIS API 전용 WebClient (옵션)
     * base URL이 미리 설정된 WebClient
     */
    @Bean(name = "kisWebClient")
    public WebClient kisWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(kisBaseUrl)
                .build();
    }

    /**
     * OpenAI API 전용 WebClient (옵션)
     */
    @Bean(name = "openaiWebClient")
    public WebClient openaiWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openaiTimeout)
                .responseTimeout(Duration.ofMillis(openaiTimeout));

        return builder
                .baseUrl("https://api.openai.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
