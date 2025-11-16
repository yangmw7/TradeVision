package com.tradevision.dto.response;

import com.tradevision.constant.InvestmentLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 응답 DTO
 * 로그인 성공 시 반환되는 JWT 토큰 및 사용자 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * Access Token (1시간 유효)
     */
    private String accessToken;

    /**
     * Refresh Token (7일 유효)
     */
    private String refreshToken;

    /**
     * 토큰 타입 (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 사용자 정보
     */
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String nickname;
        private InvestmentLevel investmentLevel;
    }
}
