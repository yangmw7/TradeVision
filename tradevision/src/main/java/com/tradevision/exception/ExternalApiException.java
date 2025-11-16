package com.tradevision.exception;

/**
 * 외부 API 호출 실패 예외
 * 한국투자증권 API, OpenAI API 등 외부 서비스 호출 실패 시 발생
 */
public class ExternalApiException extends BusinessException {

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ExternalApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
