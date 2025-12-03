package com.stocat.common.exception;

/**
 * Exception code contract for all APIs.
 * - code: mandatory, unique within the system. Use 10,000-sized ranges per API.
 * - message: mandatory, human-readable summary (client-safe).
 */
public interface ErrorCode {
    int code();

    String message();
}

