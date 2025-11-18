package com.tradevision.service;

import com.tradevision.constant.SubscriptionStatus;
import com.tradevision.entity.SubscriptionPlan;
import com.tradevision.entity.User;
import com.tradevision.entity.UserSubscription;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.repository.SubscriptionPlanRepository;
import com.tradevision.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    public List<SubscriptionPlan> getAllActivePlans() {
        return planRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    public SubscriptionPlan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
    }

    public SubscriptionPlan getPlanByName(String name) {
        return planRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found: " + name));
    }

    public UserSubscription getActiveSubscription(Long userId) {
        return subscriptionRepository.findActiveSubscriptionByUserId(userId)
                .orElse(null);
    }

    public List<UserSubscription> getUserSubscriptionHistory(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional
    public UserSubscription createFreeSubscription(User user) {
        SubscriptionPlan freePlan = getPlanByName("FREE");

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(freePlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(null)
                .autoRenew(false)
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public UserSubscription upgradeSubscription(Long userId, Long planId) {
        UserSubscription currentSubscription = getActiveSubscription(userId);

        if (currentSubscription != null) {
            currentSubscription.cancel("Upgraded to new plan");
            subscriptionRepository.save(currentSubscription);
        }

        SubscriptionPlan newPlan = getPlanById(planId);
        User user = User.builder().id(userId).build();

        LocalDateTime endDate = calculateEndDate(newPlan);

        UserSubscription newSubscription = UserSubscription.builder()
                .user(user)
                .plan(newPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(endDate)
                .autoRenew(true)
                .build();

        return subscriptionRepository.save(newSubscription);
    }

    @Transactional
    public void cancelSubscription(Long userId, String reason) {
        UserSubscription subscription = getActiveSubscription(userId);

        if (subscription == null) {
            throw new ResourceNotFoundException("No active subscription found");
        }

        subscription.cancel(reason);
        subscriptionRepository.save(subscription);

        log.info("Subscription cancelled for user: {}, reason: {}", userId, reason);
    }

    @Transactional
    public void expireSubscriptions() {
        List<UserSubscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDateTime.now());

        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.expire();
            subscriptionRepository.save(subscription);
            log.info("Subscription expired for user: {}", subscription.getUser().getId());
        }
    }

    public boolean hasActiveSubscription(Long userId) {
        UserSubscription subscription = getActiveSubscription(userId);
        return subscription != null && subscription.isActive();
    }

    public boolean canAccessFeature(Long userId, String feature) {
        UserSubscription subscription = getActiveSubscription(userId);

        if (subscription == null) {
            return false;
        }

        return subscription.isActive();
    }

    private LocalDateTime calculateEndDate(SubscriptionPlan plan) {
        LocalDateTime now = LocalDateTime.now();

        return switch (plan.getBillingPeriod()) {
            case MONTHLY -> now.plusMonths(1);
            case YEARLY -> now.plusYears(1);
            case LIFETIME -> null;
        };
    }
}
