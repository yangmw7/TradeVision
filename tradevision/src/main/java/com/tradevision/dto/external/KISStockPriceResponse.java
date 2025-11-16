package com.tradevision.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한국투자증권 API 주식 시세 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KISStockPriceResponse {

    /**
     * 응답 코드 (0: 성공)
     */
    @JsonProperty("rt_cd")
    private String resultCode;

    /**
     * 응답 메시지
     */
    @JsonProperty("msg_cd")
    private String messageCode;

    /**
     * 응답 메시지 내용
     */
    @JsonProperty("msg1")
    private String message;

    /**
     * 출력 데이터
     */
    @JsonProperty("output")
    private Output output;

    /**
     * 주식 시세 데이터
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {

        /**
         * 현재가
         */
        @JsonProperty("stck_prpr")
        private String currentPrice;

        /**
         * 전일 대비
         */
        @JsonProperty("prdy_vrss")
        private String priceChange;

        /**
         * 전일 대비율
         */
        @JsonProperty("prdy_ctrt")
        private String changeRate;

        /**
         * 누적 거래량
         */
        @JsonProperty("acml_vol")
        private String accumulatedVolume;

        /**
         * 시가
         */
        @JsonProperty("stck_oprc")
        private String openPrice;

        /**
         * 고가
         */
        @JsonProperty("stck_hgpr")
        private String highPrice;

        /**
         * 저가
         */
        @JsonProperty("stck_lwpr")
        private String lowPrice;

        /**
         * 전일 종가
         */
        @JsonProperty("stck_sdpr")
        private String previousClose;

        /**
         * 종목명
         */
        @JsonProperty("prdt_name")
        private String stockName;
    }
}
