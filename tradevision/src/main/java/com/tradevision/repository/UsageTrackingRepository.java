package com.tradevision.repository;

import com.tradevision.entity.UsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageTrackingRepository extends JpaRepository<UsageTracking, Long> {

    List<UsageTracking> findByUserIdAndActionType(Long userId, String actionType);

    List<UsageTracking> findBySessionIdAndActionType(String sessionId, String actionType);

    @Query("SELECT COUNT(u) FROM UsageTracking u WHERE u.user.id = :userId AND u.actionType = :actionType AND u.createdAt >= :startDate")
    long countByUserIdAndActionTypeAndCreatedAtAfter(Long userId, String actionType, LocalDateTime startDate);

    @Query("SELECT COUNT(u) FROM UsageTracking u WHERE u.sessionId = :sessionId AND u.actionType = :actionType AND u.createdAt >= :startDate")
    long countBySessionIdAndActionTypeAndCreatedAtAfter(String sessionId, String actionType, LocalDateTime startDate);

    @Query("SELECT COUNT(u) FROM UsageTracking u WHERE u.user.id = :userId AND u.actionType = :actionType AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) AND MONTH(u.createdAt) = MONTH(CURRENT_DATE)")
    long countByUserIdAndActionTypeInCurrentMonth(Long userId, String actionType);

    List<UsageTracking> findByCreatedAtBefore(LocalDateTime date);
}
