package com.tradevision.repository;

import com.tradevision.constant.InvestmentLevel;
import com.tradevision.constant.TechniqueCategory;
import com.tradevision.entity.ContentModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 콘텐츠 모듈 Repository
 */
@Repository
public interface ContentModuleRepository extends JpaRepository<ContentModule, Long> {

    /**
     * 활성화된 모든 모듈 조회
     *
     * @param pageable 페이징 정보
     * @return 모듈 목록
     */
    Page<ContentModule> findByIsActiveTrueOrderByDisplayOrderAsc(Pageable pageable);

    /**
     * 난이도별 모듈 조회
     *
     * @param level    난이도
     * @param pageable 페이징 정보
     * @return 모듈 목록
     */
    Page<ContentModule> findByDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(
            InvestmentLevel level, Pageable pageable);

    /**
     * 카테고리별 모듈 조회
     *
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 모듈 목록
     */
    Page<ContentModule> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(
            TechniqueCategory category, Pageable pageable);

    /**
     * 난이도와 카테고리로 모듈 조회
     *
     * @param level    난이도
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 모듈 목록
     */
    Page<ContentModule> findByDifficultyLevelAndCategoryAndIsActiveTrueOrderByDisplayOrderAsc(
            InvestmentLevel level, TechniqueCategory category, Pageable pageable);

    /**
     * ID와 활성화 여부로 조회
     *
     * @param id 모듈 ID
     * @return 모듈
     */
    Optional<ContentModule> findByIdAndIsActiveTrue(Long id);

    /**
     * 필수 모듈 조회
     *
     * @param pageable 페이징 정보
     * @return 필수 모듈 목록
     */
    Page<ContentModule> findByIsRequiredTrueAndIsActiveTrueOrderByDisplayOrderAsc(Pageable pageable);

    /**
     * 카테고리별 활성 모듈 수 조회
     *
     * @param category 카테고리
     * @return 모듈 수
     */
    long countByCategoryAndIsActiveTrue(TechniqueCategory category);

    /**
     * 난이도별 활성 모듈 수 조회
     *
     * @param level 난이도
     * @return 모듈 수
     */
    long countByDifficultyLevelAndIsActiveTrue(InvestmentLevel level);
}
