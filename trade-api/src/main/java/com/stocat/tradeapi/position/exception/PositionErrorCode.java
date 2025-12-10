package com.stocat.tradeapi.position.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum PositionErrorCode implements ErrorCode {
    NOT_FOUND_USER_POSITION(ErrorDomain.POSITION_API.offset(), "유저가 소유한 포지션을 찾을 수 없습니다");

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
