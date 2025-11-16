package com.tradevision.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenAI API 요청 DTO (GPT-4 Vision)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAIRequest {

    /**
     * 모델 이름 (gpt-4-vision-preview, gpt-4o 등)
     */
    private String model;

    /**
     * 메시지 목록
     */
    private List<Message> messages;

    /**
     * 최대 토큰 수
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 메시지 객체
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {

        /**
         * 역할 (user, assistant, system)
         */
        private String role;

        /**
         * 콘텐츠 (텍스트 또는 이미지)
         */
        private List<Content> content;
    }

    /**
     * 콘텐츠 객체 (텍스트 또는 이미지 URL)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {

        /**
         * 콘텐츠 타입 (text, image_url)
         */
        private String type;

        /**
         * 텍스트 내용
         */
        private String text;

        /**
         * 이미지 URL
         */
        @JsonProperty("image_url")
        private ImageUrl imageUrl;
    }

    /**
     * 이미지 URL 객체
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageUrl {

        /**
         * 이미지 URL 또는 Base64 데이터
         */
        private String url;

        /**
         * 이미지 상세도 (low, high, auto)
         */
        private String detail;
    }
}
