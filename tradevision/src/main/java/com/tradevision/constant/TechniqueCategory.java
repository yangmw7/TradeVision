package com.tradevision.constant;

/**
 * 학습 기법 카테고리 enum
 * 기술적 분석 기법의 카테고리를 정의
 */
public enum TechniqueCategory {
    MOVING_AVERAGE("이동평균선", "MA", "추세 추종 지표"),
    BOLLINGER_BANDS("볼린저밴드", "BB", "변동성 지표"),
    MACD("MACD", "MACD", "추세 모멘텀 지표"),
    RSI("RSI", "RSI", "모멘텀 오실레이터"),
    STOCHASTIC("스토캐스틱", "Stochastic", "모멘텀 오실레이터"),
    FIBONACCI("피보나치 되돌림", "Fibonacci", "되돌림 및 확장 지표"),
    ELLIOTT_WAVE("엘리어트 파동", "Elliott", "파동 이론"),
    CANDLE_PATTERN("캔들 패턴", "Candle", "가격 패턴 분석");

    private final String displayName;  // 화면 표시용 이름
    private final String shortName;     // 약어
    private final String type;          // 지표 유형

    TechniqueCategory(String displayName, String shortName, String type) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getType() {
        return type;
    }
}
