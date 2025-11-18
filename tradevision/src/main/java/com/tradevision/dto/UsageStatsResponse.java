package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageStatsResponse {
    private Long currentUsage;
    private Long remainingUsage;
    private Integer maxUsage;
    private Boolean isUnlimited;
    private String actionType;
}
