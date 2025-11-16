package com.tradevision.exception;

/**
 * 인증 실패 예외
 * 사용자 인증이 필요하거나 인증 정보가 유효하지 않을 때 발생
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
