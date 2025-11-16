package com.tradevision.repository;

import com.tradevision.entity.UserContentProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 콘텐츠 진행도 Repository
 */
@Repository
public interface UserContentProgressRepository extends JpaRepository<UserContentProgress, Long> {

    /**
     * 사용자별 진행도 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserContentProgress> findByUserIdOrderByLastAccessedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자의 특정 콘텐츠 진행도 조회
     *
     * @param userId    사용자 ID
     * @param contentId 콘텐츠 ID
     * @return 진행도
     */
    Optional<UserContentProgress> findByUserIdAndContentId(Long userId, Long contentId);

    /**
     * 사용자의 완료된 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserContentProgress> findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(
            Long userId, Pageable pageable);

    /**
     * 사용자의 진행중인 콘텐츠 조회 (완료되지 않았지만 시작한 콘텐츠)
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    @Query("SELECT p FROM UserContentProgress p WHERE p.user.id = :userId AND p.isCompleted = false AND p.progressPercentage > 0 ORDER BY p.lastAccessedAt DESC")
    Page<UserContentProgress> findInProgressContents(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자의 북마크된 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserContentProgress> findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(
            Long userId, Pageable pageable);

    /**
     * 사용자의 좋아요한 콘텐츠 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserContentProgress> findByUserIdAndIsLikedTrueOrderByUpdatedAtDesc(
            Long userId, Pageable pageable);

    /**
     * 사용자의 완료 개수 조회
     *
     * @param userId 사용자 ID
     * @return 완료 개수
     */
    long countByUserIdAndIsCompletedTrue(Long userId);

    /**
     * 사용자의 전체 학습 시간 조회 (초)
     *
     * @param userId 사용자 ID
     * @return 총 학습 시간 (초)
     */
    @Query("SELECT SUM(p.totalTimeSpentSeconds) FROM UserContentProgress p WHERE p.user.id = :userId")
    Long calculateTotalTimeSpent(@Param("userId") Long userId);

    /**
     * 사용자의 평균 진행률 계산
     *
     * @param userId 사용자 ID
     * @return 평균 진행률
     */
    @Query("SELECT AVG(p.progressPercentage) FROM UserContentProgress p WHERE p.user.id = :userId")
    Double calculateAverageProgress(@Param("userId") Long userId);

    /**
     * 사용자의 평균 퀴즈 점수 계산
     *
     * @param userId 사용자 ID
     * @return 평균 퀴즈 점수
     */
    @Query("SELECT AVG(p.quizScore) FROM UserContentProgress p WHERE p.user.id = :userId AND p.quizScore IS NOT NULL")
    Double calculateAverageQuizScore(@Param("userId") Long userId);

    /**
     * 사용자의 모듈별 완료 개수 조회
     *
     * @param userId   사용자 ID
     * @param moduleId 모듈 ID
     * @return 완료 개수
     */
    @Query("SELECT COUNT(p) FROM UserContentProgress p WHERE p.user.id = :userId AND p.content.module.id = :moduleId AND p.isCompleted = true")
    long countCompletedByModule(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    /**
     * 사용자의 모듈별 전체 콘텐츠 진행도 조회
     *
     * @param userId   사용자 ID
     * @param moduleId 모듈 ID
     * @return 진행도 목록
     */
    @Query("SELECT p FROM UserContentProgress p WHERE p.user.id = :userId AND p.content.module.id = :moduleId ORDER BY p.content.displayOrder ASC")
    List<UserContentProgress> findByUserIdAndModuleId(@Param("userId") Long userId, @Param("moduleId") Long moduleId);

    /**
     * 콘텐츠별 완료한 사용자 수 조회
     *
     * @param contentId 콘텐츠 ID
     * @return 완료한 사용자 수
     */
    long countByContentIdAndIsCompletedTrue(Long contentId);
}
