package com.stocat.common.exception;

/**
 * 10,000-sized code ranges per API/module.
 * Each API defines its own enum implementing ErrorCode within its range.
 * <p>
 * Example usage in auth-api:
 * enum AuthErrors implements ErrorCode {
 * INVALID_CREDENTIALS(ErrorDomain.AUTH.offset() + 1, "invalid credentials");
 * private final int code; private final String message;
 * AuthErrors(int code, String message) { this.code = code; this.message = message; }
 * public int code() { return code; }
 * public String message() { return message; }
 * }
 */
public enum ErrorDomain {
    AUTH(10_000),
    TRADE_API(20_000),
    TRADE_WS(30_000),
    ASSET_SCRAPER(40_000),
    POSITION_API(50_000),
    CASH_API(60_000),
    COMMON(90_000);

    private final int offset;

    ErrorDomain(int offset) {
        this.offset = offset;
    }

    public int offset() {
        return offset;
    }
}

