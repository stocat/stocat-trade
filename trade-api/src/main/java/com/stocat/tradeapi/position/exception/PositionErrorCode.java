package com.stocat.tradeapi.position.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum PositionErrorCode implements ErrorCode {
    NOT_FOUND_USER_POSITION(ErrorDomain.POSITION_API.offset(), "유저가 소유한 포지션을 찾을 수 없습니다"),
    INVALID_POSITION_QUANTITY(ErrorDomain.POSITION_API.offset() + 1, "거래 수량은 0일 수 없습니다"),
    POSITION_NOT_FOUND_FOR_SELL(ErrorDomain.POSITION_API.offset() + 2, "보유 포지션 없이 매도할 수 없습니다"),
    INSUFFICIENT_POSITION_QUANTITY(ErrorDomain.POSITION_API.offset() + 3, "보유 수량보다 많이 매도할 수 없습니다");

    private final int code;
    private final String message;

    PositionErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
