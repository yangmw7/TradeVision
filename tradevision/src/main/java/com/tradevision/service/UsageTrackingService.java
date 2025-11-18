package com.tradevision.service;

import com.tradevision.entity.UsageTracking;
import com.tradevision.entity.User;
import com.tradevision.entity.UserSubscription;
import com.tradevision.repository.UsageTrackingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageTrackingService {

    private final UsageTrackingRepository usageRepository;
    private final SubscriptionService subscriptionService;

    @Transactional
    public void trackUsage(Long userId, String sessionId, String actionType, Long resourceId, String metadata, HttpServletRequest request) {
        User user = userId != null ? new User() : null;
        if (user != null) {
            user.setId(userId);
        }

        UsageTracking usage = UsageTracking.builder()
                .user(user)
                .sessionId(sessionId)
                .ipAddress(getClientIp(request))
                .actionType(actionType)
                .resourceId(resourceId)
                .metadata(metadata)
                .build();

        usageRepository.save(usage);
        log.debug("Usage tracked: {} for user/session: {}/{}", actionType, userId, sessionId);
    }

    public boolean canPerformAction(Long userId, String sessionId, String actionType) {
        if (userId != null) {
            UserSubscription subscription = subscriptionService.getActiveSubscription(userId);

            if (subscription == null) {
                return false;
            }

            if (subscription.getPlan().hasUnlimitedAnalyses()) {
                return true;
            }

            long usageCount = usageRepository.countByUserIdAndActionTypeInCurrentMonth(userId, actionType);
            return usageCount < subscription.getPlan().getMaxAnalysesPerMonth();
        } else if (sessionId != null) {
            long usageCount = usageRepository.countBySessionIdAndActionTypeAndCreatedAtAfter(
                    sessionId, actionType, LocalDateTime.now().minusDays(30));
            return usageCount < 5;
        }

        return false;
    }

    public long getRemainingUsage(Long userId, String sessionId, String actionType) {
        if (userId != null) {
            UserSubscription subscription = subscriptionService.getActiveSubscription(userId);

            if (subscription == null) {
                return 0;
            }

            if (subscription.getPlan().hasUnlimitedAnalyses()) {
                return -1;
            }

            long usageCount = usageRepository.countByUserIdAndActionTypeInCurrentMonth(userId, actionType);
            long maxUsage = subscription.getPlan().getMaxAnalysesPerMonth();
            return Math.max(0, maxUsage - usageCount);
        } else if (sessionId != null) {
            long usageCount = usageRepository.countBySessionIdAndActionTypeAndCreatedAtAfter(
                    sessionId, actionType, LocalDateTime.now().minusDays(30));
            return Math.max(0, 5 - usageCount);
        }

        return 0;
    }

    public long getCurrentMonthUsage(Long userId, String actionType) {
        return usageRepository.countByUserIdAndActionTypeInCurrentMonth(userId, actionType);
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Transactional
    public void cleanupOldUsageData(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        usageRepository.findByCreatedAtBefore(cutoffDate).forEach(usageRepository::delete);
        log.info("Cleaned up usage data older than {} days", daysToKeep);
    }
}
