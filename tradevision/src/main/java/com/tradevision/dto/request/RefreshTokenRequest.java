package com.tradevision.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 * Refresh Token을 사용하여 새 Access Token 발급 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    /**
     * Refresh Token 값
     */
    @NotBlank(message = "Refresh Token은 필수입니다")
    private String refreshToken;
}
