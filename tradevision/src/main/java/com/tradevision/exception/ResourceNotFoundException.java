package com.tradevision.exception;

/**
 * 리소스 없음 예외
 * 요청한 리소스(사용자, 분석 결과, 학습 콘텐츠 등)를 찾을 수 없을 때 발생
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
