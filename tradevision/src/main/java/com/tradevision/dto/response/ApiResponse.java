package com.tradevision.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 공통 응답 래퍼 클래스
 * 모든 API 응답을 일관된 형식으로 반환
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값은 JSON에서 제외
public class ApiResponse<T> {

    private boolean success;     // 성공 여부
    private String message;      // 응답 메시지 (한국어)
    private T data;              // 응답 데이터
    private String errorCode;    // 에러 코드 (실패 시)

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }

    /**
     * 성공 응답 생성 (메시지 + 데이터)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    /**
     * 실패 응답 생성 (데이터 포함 - Validation 에러 등)
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, T data) {
        return new ApiResponse<>(false, message, data, errorCode);
    }
}
