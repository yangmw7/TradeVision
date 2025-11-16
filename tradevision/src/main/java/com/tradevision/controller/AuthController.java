package com.tradevision.controller;

import com.tradevision.dto.request.LoginRequest;
import com.tradevision.dto.request.RefreshTokenRequest;
import com.tradevision.dto.request.SignupRequest;
import com.tradevision.dto.response.ApiResponse;
import com.tradevision.dto.response.AuthResponse;
import com.tradevision.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API 컨트롤러
 * 회원가입, 로그인, 토큰 갱신 엔드포인트 제공
 */
@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * POST /api/auth/signup
     *
     * @param request 회원가입 요청 (이메일, 비밀번호, 닉네임)
     * @return 201 Created, 생성된 사용자 ID
     */
    @Operation(summary = "회원가입", description = "새 사용자 계정을 생성합니다")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다", userId));
    }

    /**
     * 로그인 API
     * POST /api/auth/login
     *
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return 200 OK, JWT 토큰 및 사용자 정보
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("로그인에 성공했습니다", authResponse)
        );
    }

    /**
     * 토큰 갱신 API
     * POST /api/auth/refresh
     *
     * @param request Refresh Token 요청
     * @return 200 OK, 새 Access Token
     */
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token을 발급받습니다")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refresh(request);

        return ResponseEntity.ok(
                ApiResponse.success("토큰이 갱신되었습니다", authResponse)
        );
    }
}
