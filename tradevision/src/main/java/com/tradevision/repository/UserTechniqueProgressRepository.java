package com.tradevision.repository;

import com.tradevision.constant.ProgressStatus;
import com.tradevision.entity.UserTechniqueProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 매매기법 진행도 Repository
 */
@Repository
public interface UserTechniqueProgressRepository extends JpaRepository<UserTechniqueProgress, Long> {

    /**
     * 사용자별 진행도 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserTechniqueProgress> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자의 특정 기법 진행도 조회
     *
     * @param userId      사용자 ID
     * @param techniqueId 기법 ID
     * @return 진행도
     */
    Optional<UserTechniqueProgress> findByUserIdAndTechniqueId(Long userId, Long techniqueId);

    /**
     * 사용자의 상태별 진행도 조회
     *
     * @param userId   사용자 ID
     * @param status   상태
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserTechniqueProgress> findByUserIdAndStatusOrderByUpdatedAtDesc(
            Long userId, ProgressStatus status, Pageable pageable);

    /**
     * 사용자의 북마크된 기법 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserTechniqueProgress> findByUserIdAndIsBookmarkedTrueOrderByUpdatedAtDesc(
            Long userId, Pageable pageable);

    /**
     * 사용자의 완료된 기법 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserTechniqueProgress> findByUserIdAndIsCompletedTrueOrderByUpdatedAtDesc(
            Long userId, Pageable pageable);

    /**
     * 사용자의 완료 개수 조회
     *
     * @param userId 사용자 ID
     * @return 완료 개수
     */
    long countByUserIdAndIsCompletedTrue(Long userId);

    /**
     * 사용자의 진행중인 기법 개수
     *
     * @param userId 사용자 ID
     * @return 진행중 개수
     */
    long countByUserIdAndStatus(Long userId, ProgressStatus status);

    /**
     * 사용자의 전체 진행률 계산
     *
     * @param userId 사용자 ID
     * @return 평균 진행률
     */
    @Query("SELECT AVG(p.progressPercentage) FROM UserTechniqueProgress p WHERE p.user.id = :userId")
    Double calculateAverageProgress(@Param("userId") Long userId);

    /**
     * 사용자의 평점 기준 기법 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 진행도 목록
     */
    Page<UserTechniqueProgress> findByUserIdAndUserRatingIsNotNullOrderByUserRatingDescUpdatedAtDesc(
            Long userId, Pageable pageable);
}
