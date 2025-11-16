package com.tradevision.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한국투자증권 API OAuth 토큰 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KISTokenResponse {

    /**
     * Access Token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 토큰 타입 (Bearer)
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 만료 시간 (초)
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * Access Token 만료 시간
     */
    @JsonProperty("access_token_token_expired")
    private String accessTokenExpired;
}
