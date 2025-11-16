package com.tradevision.constant;

/**
 * 학습 콘텐츠 타입
 */
public enum ContentType {
    /**
     * 문서/아티클
     */
    ARTICLE("문서"),

    /**
     * 비디오
     */
    VIDEO("비디오"),

    /**
     * 퀴즈/테스트
     */
    QUIZ("퀴즈"),

    /**
     * 인터랙티브 실습
     */
    INTERACTIVE("실습"),

    /**
     * 케이스 스터디
     */
    CASE_STUDY("사례 분석");

    private final String displayName;

    ContentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
