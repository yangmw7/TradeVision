package com.tradevision.repository;

import com.tradevision.constant.ContentType;
import com.tradevision.entity.LearningContent;
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
 * 학습 콘텐츠 Repository
 */
@Repository
public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {

    /**
     * 모듈별 콘텐츠 조회
     *
     * @param moduleId 모듈 ID
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @Query("SELECT c FROM LearningContent c WHERE c.module.id = :moduleId AND c.isActive = true ORDER BY c.displayOrder ASC")
    Page<LearningContent> findByModuleIdAndIsActiveTrue(@Param("moduleId") Long moduleId, Pageable pageable);

    /**
     * 모듈별 콘텐츠 전체 조회 (순서대로)
     *
     * @param moduleId 모듈 ID
     * @return 콘텐츠 목록
     */
    @Query("SELECT c FROM LearningContent c WHERE c.module.id = :moduleId AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<LearningContent> findAllByModuleIdAndIsActiveTrue(@Param("moduleId") Long moduleId);

    /**
     * 콘텐츠 타입별 조회
     *
     * @param contentType 콘텐츠 타입
     * @param pageable    페이징 정보
     * @return 콘텐츠 목록
     */
    Page<LearningContent> findByContentTypeAndIsActiveTrueOrderByCreatedAtDesc(
            ContentType contentType, Pageable pageable);

    /**
     * 무료 콘텐츠 조회
     *
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    Page<LearningContent> findByIsFreeTrueAndIsActiveTrueOrderByViewCountDesc(Pageable pageable);

    /**
     * 인기 콘텐츠 조회 (조회수 기준)
     *
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isActive = true ORDER BY c.viewCount DESC")
    List<LearningContent> findTopByViewCount(Pageable pageable);

    /**
     * 추천 콘텐츠 조회 (좋아요 수 기준)
     *
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isActive = true ORDER BY c.likeCount DESC")
    List<LearningContent> findTopByLikeCount(Pageable pageable);

    /**
     * 키워드로 콘텐츠 검색
     *
     * @param keyword  검색어
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @Query("SELECT c FROM LearningContent c WHERE c.isActive = true AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.viewCount DESC")
    Page<LearningContent> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * ID와 활성화 여부로 조회
     *
     * @param id 콘텐츠 ID
     * @return 콘텐츠
     */
    Optional<LearningContent> findByIdAndIsActiveTrue(Long id);

    /**
     * 조회수 증가
     *
     * @param id 콘텐츠 ID
     */
    @Modifying
    @Query("UPDATE LearningContent c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 좋아요 수 증가
     *
     * @param id 콘텐츠 ID
     */
    @Modifying
    @Query("UPDATE LearningContent c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 좋아요 수 감소
     *
     * @param id 콘텐츠 ID
     */
    @Modifying
    @Query("UPDATE LearningContent c SET c.likeCount = c.likeCount - 1 WHERE c.id = :id AND c.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    /**
     * 모듈별 콘텐츠 수 조회
     *
     * @param moduleId 모듈 ID
     * @return 콘텐츠 수
     */
    long countByModuleIdAndIsActiveTrue(Long moduleId);
}
