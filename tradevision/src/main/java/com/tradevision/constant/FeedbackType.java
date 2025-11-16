package com.tradevision.constant;

/**
 * 분석 피드백 타입 enum
 * 사용자가 AI 차트 분석 결과에 대해 남기는 피드백 종류
 */
public enum FeedbackType {
    SUCCESS("성공", "분석 결과가 맞았음"),
    FAIL("실패", "분석 결과가 틀렸음"),
    NONE("없음", "아직 피드백 없음");

    private final String displayName;  // 화면 표시용 이름
    private final String description;   // 피드백 설명

    FeedbackType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
