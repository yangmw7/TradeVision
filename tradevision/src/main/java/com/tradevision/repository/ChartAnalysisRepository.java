package com.tradevision.repository;

import com.tradevision.entity.ChartAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 차트 분석 Repository
 */
@Repository
public interface ChartAnalysisRepository extends JpaRepository<ChartAnalysis, Long> {

    /**
     * 사용자별 분석 히스토리 조회 (최신순, 페이징)
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 분석 히스토리 페이지
     */
    Page<ChartAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자별 특정 종목 분석 히스토리 조회
     *
     * @param userId    사용자 ID
     * @param stockCode 종목 코드
     * @param pageable  페이징 정보
     * @return 분석 히스토리 페이지
     */
    Page<ChartAnalysis> findByUserIdAndStockCodeOrderByCreatedAtDesc(
            Long userId, String stockCode, Pageable pageable);

    /**
     * 사용자 ID와 분석 ID로 조회
     *
     * @param id     분석 ID
     * @param userId 사용자 ID
     * @return 분석 결과
     */
    Optional<ChartAnalysis> findByIdAndUserId(Long id, Long userId);

    /**
     * 사용자의 일일 분석 횟수 조회
     *
     * @param userId    사용자 ID
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 분석 횟수
     */
    @Query("SELECT COUNT(c) FROM ChartAnalysis c WHERE c.user.id = :userId " +
            "AND c.createdAt BETWEEN :startDate AND :endDate")
    long countByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 사용자의 오늘 분석 횟수 조회
     *
     * @param userId 사용자 ID
     * @param today  오늘 시작 시간
     * @return 오늘 분석 횟수
     */
    @Query("SELECT COUNT(c) FROM ChartAnalysis c WHERE c.user.id = :userId " +
            "AND c.createdAt >= :today")
    long countTodayAnalyses(@Param("userId") Long userId, @Param("today") LocalDateTime today);
}
