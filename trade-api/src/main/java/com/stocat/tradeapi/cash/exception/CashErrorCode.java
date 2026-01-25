package com.stocat.tradeapi.cash.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum CashErrorCode implements ErrorCode {
    INVALID_CASH_AMOUNT(ErrorDomain.CASH_API.offset(), "현금 금액이 올바르지 않습니다."),
    CASH_BALANCE_NOT_FOUND(ErrorDomain.CASH_API.offset() + 1, "현금 잔액 정보를 찾을 수 없습니다."),
    INSUFFICIENT_CASH_BALANCE(ErrorDomain.CASH_API.offset() + 2, "가용 현금이 부족합니다."),
    CASH_HOLDING_NOT_FOUND(ErrorDomain.CASH_API.offset() + 3, "해당 주문과 연결된 현금 홀딩이 없습니다."),
    CASH_HOLDING_ALREADY_FINALIZED(ErrorDomain.CASH_API.offset() + 4, "이미 처리된 현금 홀딩입니다.");

    private final int code;
    private final String message;

    CashErrorCode(int code, String message) {
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
