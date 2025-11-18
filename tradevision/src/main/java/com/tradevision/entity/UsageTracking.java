package com.tradevision.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(columnDefinition = "JSON")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static final String ACTION_CHART_ANALYSIS = "CHART_ANALYSIS";
    public static final String ACTION_CONTENT_VIEW = "CONTENT_VIEW";
    public static final String ACTION_TECHNIQUE_VIEW = "TECHNIQUE_VIEW";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_SIGNUP = "SIGNUP";
}
