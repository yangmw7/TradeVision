package com.tradevision.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private Long planId;
    private String paymentMethod;
}
