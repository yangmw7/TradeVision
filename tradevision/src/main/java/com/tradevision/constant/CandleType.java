package com.tradevision.constant;

/**
 * 봉 종류 enum
 * 주식 차트의 다양한 시간 단위 봉을 정의
 */
public enum CandleType {
    M1("1분봉", "1분"),
    M5("5분봉", "5분"),
    M15("15분봉", "15분"),
    H1("60분봉", "1시간"),
    D("일봉", "1일"),
    W("주봉", "1주"),
    M("월봉", "1개월");

    private final String displayName;  // 화면 표시용 이름
    private final String duration;      // 기간 표시

    CandleType(String displayName, String duration) {
        this.displayName = displayName;
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDuration() {
        return duration;
    }

    /**
     * 문자열로부터 CandleType 찾기
     * @param value 봉 종류 문자열
     * @return 매칭되는 CandleType, 없으면 null
     */
    public static CandleType fromString(String value) {
        for (CandleType type : CandleType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
