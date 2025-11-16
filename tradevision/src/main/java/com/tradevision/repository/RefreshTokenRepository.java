package com.tradevision.repository;

import com.tradevision.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Refresh Token Repository
 * RefreshToken 엔티티에 대한 데이터베이스 접근
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 값으로 Refresh Token 찾기
     * @param token 토큰 값
     * @return RefreshToken Optional
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 Refresh Token 삭제
     * @param userId 사용자 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 사용자 ID로 Refresh Token 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재하면 true
     */
    boolean existsByUserId(Long userId);
}
