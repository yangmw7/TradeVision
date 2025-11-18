package com.tradevision.dto;

import com.tradevision.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanResponse {
    private Long id;
    private String name;
    private String nameKo;
    private String description;
    private BigDecimal price;
    private String billingPeriod;
    private List<String> features;
    private Integer maxAnalysesPerMonth;
    private Boolean isActive;

    public static SubscriptionPlanResponse from(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .nameKo(plan.getNameKo())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingPeriod(plan.getBillingPeriod().name())
                .features(plan.getFeatureList())
                .maxAnalysesPerMonth(plan.getMaxAnalysesPerMonth())
                .isActive(plan.getIsActive())
                .build();
    }
}
