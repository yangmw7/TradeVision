package com.tradevision.controller;

import com.tradevision.dto.*;
import com.tradevision.entity.SubscriptionPlan;
import com.tradevision.entity.User;
import com.tradevision.entity.UserSubscription;
import com.tradevision.security.UserPrincipal;
import com.tradevision.service.SubscriptionService;
import com.tradevision.service.UsageTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UsageTrackingService usageTrackingService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getAllPlans() {
        List<SubscriptionPlan> plans = subscriptionService.getAllActivePlans();
        List<SubscriptionPlanResponse> response = plans.stream()
                .map(SubscriptionPlanResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserSubscriptionResponse>> getCurrentSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UserSubscription subscription = subscriptionService.getActiveSubscription(userPrincipal.getId());

        if (subscription == null) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        return ResponseEntity.ok(ApiResponse.success(UserSubscriptionResponse.from(subscription)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<UserSubscriptionResponse>>> getSubscriptionHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<UserSubscription> subscriptions = subscriptionService.getUserSubscriptionHistory(userPrincipal.getId());
        List<UserSubscriptionResponse> response = subscriptions.stream()
                .map(UserSubscriptionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<UserSubscriptionResponse>> subscribe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody SubscriptionRequest request) {

        UserSubscription subscription = subscriptionService.upgradeSubscription(
                userPrincipal.getId(),
                request.getPlanId()
        );

        return ResponseEntity.ok(ApiResponse.success(UserSubscriptionResponse.from(subscription)));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelSubscription(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String reason) {

        subscriptionService.cancelSubscription(userPrincipal.getId(), reason);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully"));
    }

    @GetMapping("/usage/{actionType}")
    public ResponseEntity<ApiResponse<UsageStatsResponse>> getUsageStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String actionType,
            @RequestParam(required = false) String sessionId) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        long currentUsage = usageTrackingService.getCurrentMonthUsage(userId, actionType);
        long remainingUsage = usageTrackingService.getRemainingUsage(userId, sessionId, actionType);

        UserSubscription subscription = userId != null ?
                subscriptionService.getActiveSubscription(userId) : null;

        Integer maxUsage = subscription != null ?
                subscription.getPlan().getMaxAnalysesPerMonth() : 5;
        Boolean isUnlimited = subscription != null && subscription.getPlan().hasUnlimitedAnalyses();

        UsageStatsResponse stats = UsageStatsResponse.builder()
                .currentUsage(currentUsage)
                .remainingUsage(remainingUsage)
                .maxUsage(maxUsage)
                .isUnlimited(isUnlimited)
                .actionType(actionType)
                .build();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
