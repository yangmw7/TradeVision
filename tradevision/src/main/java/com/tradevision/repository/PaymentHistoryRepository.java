package com.tradevision.repository;

import com.tradevision.constant.PaymentStatus;
import com.tradevision.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    List<PaymentHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<PaymentHistory> findByTransactionId(String transactionId);

    List<PaymentHistory> findByUserIdAndStatus(Long userId, PaymentStatus status);

    List<PaymentHistory> findBySubscriptionId(Long subscriptionId);
}
