package com.tradevision.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenAI API 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIResponse {

    /**
     * 응답 ID
     */
    private String id;

    /**
     * 객체 타입 (chat.completion)
     */
    private String object;

    /**
     * 생성 시간 (Unix timestamp)
     */
    private Long created;

    /**
     * 사용된 모델
     */
    private String model;

    /**
     * 선택지 목록 (보통 1개)
     */
    private List<Choice> choices;

    /**
     * 토큰 사용량 정보
     */
    private Usage usage;

    /**
     * 선택지 객체
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {

        /**
         * 인덱스
         */
        private Integer index;

        /**
         * 메시지
         */
        private Message message;

        /**
         * 종료 이유 (stop, length, content_filter)
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * 메시지 객체
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        /**
         * 역할 (assistant)
         */
        private String role;

        /**
         * 콘텐츠 (분석 결과 텍스트)
         */
        private String content;
    }

    /**
     * 토큰 사용량 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        /**
         * 프롬프트 토큰 수
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 완성 토큰 수
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 총 토큰 수
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
