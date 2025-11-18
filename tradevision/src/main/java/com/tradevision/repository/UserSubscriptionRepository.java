package com.tradevision.repository;

import com.tradevision.constant.SubscriptionStatus;
import com.tradevision.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Optional<UserSubscription> findActiveSubscriptionByUserId(Long userId);

    List<UserSubscription> findByUserId(Long userId);

    List<UserSubscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDateTime date);

    @Query("SELECT COUNT(s) FROM UserSubscription s WHERE s.user.id = :userId AND s.plan.id = :planId AND s.status = 'ACTIVE'")
    long countActiveSubscriptionsByUserIdAndPlanId(Long userId, Long planId);
}
