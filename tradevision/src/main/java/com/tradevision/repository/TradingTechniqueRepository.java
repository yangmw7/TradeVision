package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.TradingTechnique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 매매기법 Repository
 */
@Repository
public interface TradingTechniqueRepository extends JpaRepository<TradingTechnique, Long> {

    /**
     * 활성화된 모든 기법 조회 (페이징)
     *
     * @param pageable 페이징 정보
     * @return 기법 목록
     */
    Page<TradingTechnique> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 난이도별 기법 조회
     *
     * @param level    난이도
     * @param pageable 페이징 정보
     * @return 기법 목록
     */
    Page<TradingTechnique> findByDifficultyLevelAndIsActiveTrueOrderByRecommendationCountDesc(
            InvestmentLevel level, Pageable pageable);

    /**
     * 카테고리별 기법 조회
     *
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 기법 목록
     */
    Page<TradingTechnique> findByCategoryAndIsActiveTrueOrderByViewCountDesc(
            TechniqueCategory category, Pageable pageable);

    /**
     * 난이도와 카테고리로 기법 조회
     *
     * @param level    난이도
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 기법 목록
     */
    Page<TradingTechnique> findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByRecommendationCountDesc(
            InvestmentLevel level, TechniqueCategory category, Pageable pageable);

    /**
     * 기법 이름으로 검색 (LIKE)
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @return 기법 목록
     */
    @Query("SELECT t FROM TradingTechnique t WHERE t.isActive = true AND " +
            "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.nameEn) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.viewCount DESC")
    Page<TradingTechnique> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 기법 조회 (조회수 기준)
     *
     * @param limit 조회 개수
     * @return 기법 목록
     */
    @Query("SELECT t FROM TradingTechnique t WHERE t.isActive = true " +
            "ORDER BY t.viewCount DESC")
    List<TradingTechnique> findTopByViewCount(Pageable pageable);

    /**
     * 추천 기법 조회 (추천수 기준)
     *
     * @param pageable 페이징 정보 (limit 포함)
     * @return 기법 목록
     */
    @Query("SELECT t FROM TradingTechnique t WHERE t.isActive = true " +
            "ORDER BY t.recommendationCount DESC")
    List<TradingTechnique> findTopByRecommendationCount(Pageable pageable);

    /**
     * 조회수 증가
     *
     * @param id 기법 ID
     */
    @Modifying
    @Query("UPDATE TradingTechnique t SET t.viewCount = t.viewCount + 1 WHERE t.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 추천수 증가
     *
     * @param id 기법 ID
     */
    @Modifying
    @Query("UPDATE TradingTechnique t SET t.recommendationCount = t.recommendationCount + 1 WHERE t.id = :id")
    void incrementRecommendationCount(@Param("id") Long id);

    /**
     * ID와 활성화 여부로 조회
     *
     * @param id 기법 ID
     * @return 기법
     */
    Optional<TradingTechnique> findByIdAndIsActiveTrue(Long id);
}
