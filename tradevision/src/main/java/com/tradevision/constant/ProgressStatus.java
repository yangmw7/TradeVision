package com.tradevision.constant;

/**
 * 학습 진행 상태
 */
public enum ProgressStatus {
    /**
     * 미시작
     */
    NOT_STARTED("미시작"),

    /**
     * 진행중
     */
    IN_PROGRESS("진행중"),

    /**
     * 완료
     */
    COMPLETED("완료"),

    /**
     * 복습중
     */
    REVIEWING("복습중");

    private final String displayName;

    ProgressStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
