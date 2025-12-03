package com.stocat.common.exception;

import java.util.Map;

/**
 * Base runtime exception carrying an ErrorCode.
 * Keep it minimal so each API can implement its own hierarchies/handlers.
 */
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
        this.details = null;
    }

    public ApiException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.details = details;
    }

    public ApiException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}

