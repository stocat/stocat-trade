package com.stocat.common.response;

import com.stocat.common.exception.ErrorCode;

/**
 * 공통 API 응답 규격 (code/message/data)을 설명하는 레코드.
 * <p>
 * - code: 정수 상태 코드 (성공 시 1000)<br>
 * - message: 사용자 친화적 메시지 (성공 시 "성공")<br>
 * - data: 도메인별 성공/실패 payload
 * <p>
 * 각 API 모듈은 필요 시 이 클래스를 직접 사용하거나,
 * 동일한 필드 구성을 갖는 전용 DTO를 정의해 사용할 수 있습니다.
 */
public record ApiResponse<T>(
        int code,
        String message,
        T data
) {

    public static final int SUCCESS_CODE = 1000;
    public static final String SUCCESS_MESSAGE = "성공";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return failure(errorCode, null);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, T data) {
        return new ApiResponse<>(errorCode.code(), errorCode.message(), data);
    }
}
