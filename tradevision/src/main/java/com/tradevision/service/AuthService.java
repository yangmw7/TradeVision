package com.tradevision.service;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.dto.request.LoginRequest;
import com.tradevision.dto.request.RefreshTokenRequest;
import com.tradevision.dto.request.SignupRequest;
import com.tradevision.dto.response.AuthResponse;
import com.tradevision.entity.RefreshToken;
import com.tradevision.entity.User;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.UnauthorizedException;
import com.tradevision.repository.RefreshTokenRepository;
import com.tradevision.repository.UserRepository;
import com.tradevision.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 인증 서비스
 * 회원가입, 로그인, 토큰 갱신 등 인증 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * 이메일 중복 확인 → 비밀번호 암호화 → 사용자 저장
     *
     * @param request 회원가입 요청 정보
     * @return 생성된 사용자 ID
     */
    @Transactional
    public Long signup(SignupRequest request) {
        log.info("회원가입 시도: {}", request.getEmail());

        // 1. 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화 (BCrypt, strength 10)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 사용자 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .investmentLevel(
                        request.getInvestmentLevel() != null ?
                                request.getInvestmentLevel() : InvestmentLevel.BEGINNER
                )
                .build();

        // 4. 데이터베이스 저장
        User savedUser = userRepository.save(user);

        log.info("회원가입 성공: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

        return savedUser.getId();
    }

    /**
     * 로그인
     * 이메일/비밀번호 검증 → JWT Access Token, Refresh Token 발급
     *
     * @param request 로그인 요청 정보
     * @return AuthResponse (accessToken, refreshToken, 사용자 정보)
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("로그인 시도: {}", request.getEmail());

        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());

        // 4. Refresh Token 생성
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // 5. Refresh Token 저장 (기존 토큰 삭제 후 새로 저장)
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidity())))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("로그인 성공: {} (ID: {})", user.getEmail(), user.getId());

        // 6. AuthResponse 생성 및 반환
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .investmentLevel(user.getInvestmentLevel())
                        .build())
                .build();
    }

    /**
     * 토큰 갱신
     * Refresh Token 검증 → 새 Access Token 발급
     *
     * @param request Refresh Token 요청
     * @return AuthResponse (새 accessToken, 기존 refreshToken)
     */
    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("토큰 갱신 시도");

        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 데이터베이스에서 Refresh Token 찾기
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_TOKEN));

        // 3. Refresh Token 만료 확인
        if (refreshToken.isExpired()) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_TOKEN);
        }

        // 4. 사용자 정보 가져오기
        User user = refreshToken.getUser();

        // 5. 새 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());

        log.info("토큰 갱신 성공: {}", user.getEmail());

        // 6. AuthResponse 생성 및 반환 (Refresh Token은 재사용)
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .investmentLevel(user.getInvestmentLevel())
                        .build())
                .build();
    }
}
