package com.tradevision.repository;

import com.tradevision.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<SubscriptionPlan> findByName(String name);

    Optional<SubscriptionPlan> findByNameIgnoreCase(String name);
}
