package com.tradevision.exception;

/**
 * 에러 코드 enum
 * 애플리케이션 전체에서 사용하는 에러 코드 정의
 */
public enum ErrorCode {
    // 인증 관련 (1xxx)
    INVALID_CREDENTIALS("1001", "이메일 또는 비밀번호가 올바르지 않습니다"),
    DUPLICATE_EMAIL("1002", "이미 사용 중인 이메일입니다"),
    INVALID_TOKEN("1003", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN("1004", "만료된 토큰입니다"),
    UNAUTHORIZED("1005", "인증이 필요합니다"),
    FORBIDDEN("1006", "접근 권한이 없습니다"),

    // 사용자 관련 (2xxx)
    USER_NOT_FOUND("2001", "사용자를 찾을 수 없습니다"),
    INVALID_PASSWORD("2002", "비밀번호가 올바르지 않습니다"),
    INVALID_PASSWORD_FORMAT("2003", "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다"),
    ANALYSIS_NOT_FOUND("2004", "분석 결과를 찾을 수 없습니다"),

    // 주식 데이터 관련 (3xxx)
    INVALID_STOCK_CODE("3001", "유효하지 않은 종목 코드입니다"),
    STOCK_DATA_NOT_FOUND("3002", "주식 데이터를 찾을 수 없습니다"),
    KIS_API_ERROR("3003", "주식 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요"),
    KIS_API_RATE_LIMIT("3004", "요청 한도를 초과했습니다. 1분 후 다시 시도해주세요"),

    // 차트 분석 관련 (4xxx)
    INVALID_IMAGE_FORMAT("4001", "JPG 또는 PNG 형식의 이미지만 업로드 가능합니다"),
    IMAGE_SIZE_EXCEEDED("4002", "이미지 크기는 5MB 이하로 제한됩니다"),
    AI_ANALYSIS_FAILED("4003", "AI 분석에 실패했습니다. 다시 시도해주세요"),
    CHART_NOT_DETECTED("4004", "업로드한 이미지에서 차트를 찾을 수 없습니다. 차트 이미지를 업로드해주세요"),
    OPENAI_API_ERROR("4006", "AI 분석 서비스에 문제가 발생했습니다"),
    DAILY_LIMIT_EXCEEDED("4007", "오늘의 AI 분석 한도를 초과했습니다. 내일 다시 시도해주세요"),

    // 학습 관련 (5xxx)
    LEARNING_CONTENT_NOT_FOUND("5001", "학습 콘텐츠를 찾을 수 없습니다"),
    TECHNIQUE_NOT_FOUND("5002", "매매기법을 찾을 수 없습니다"),
    PROGRESS_NOT_FOUND("5003", "학습 진행도를 찾을 수 없습니다"),

    // 시스템 관련 (9xxx)
    INTERNAL_SERVER_ERROR("9001", "서버 내부 오류가 발생했습니다"),
    EXTERNAL_API_ERROR("9002", "외부 API 호출 중 오류가 발생했습니다"),
    EXTERNAL_API_UNAVAILABLE("9003", "외부 API 서버에 일시적으로 접근할 수 없습니다"),
    DATABASE_ERROR("9004", "데이터베이스 오류가 발생했습니다"),
    INVALID_INPUT("9005", "잘못된 입력값입니다"),
    RESOURCE_NOT_FOUND("9006", "요청한 리소스를 찾을 수 없습니다"),
    NETWORK_ERROR("9007", "인터넷 연결을 확인해주세요"),
    STOCK_NOT_FOUND("3005", "종목을 찾을 수 없습니다");

    private final String code;     // 에러 코드
    private final String message;  // 한국어 에러 메시지

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
