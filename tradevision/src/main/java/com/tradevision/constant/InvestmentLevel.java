package com.tradevision.constant;

/**
 * 투자 경험 수준 enum
 * 사용자의 주식 투자 경험 레벨을 구분
 */
public enum InvestmentLevel {
    BEGINNER("초보자", "주식 투자 경험 1년 미만"),
    INTERMEDIATE("중급자", "주식 투자 경험 1~3년"),
    ADVANCED("고급자", "주식 투자 경험 3년 이상");

    private final String displayName;  // 화면 표시용 이름
    private final String description;   // 레벨 설명

    InvestmentLevel(String displayName, String description) {
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
