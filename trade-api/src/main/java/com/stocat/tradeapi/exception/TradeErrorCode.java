package com.stocat.tradeapi.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum TradeErrorCode implements ErrorCode {
    INTERNAL_ERROR(ErrorDomain.TRADE_API.offset(), "서버 에러가 발생했습니다."),
    INVALID_ORDER_SIDE(ErrorDomain.TRADE_API.offset() + 1, "알 수 없는 주문 요청입니다."),
    ASSET_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 2, "존재하지 않는 종목입니다."),
    NOT_DAILY_PICK_ASSET(ErrorDomain.TRADE_API.offset() + 3, "데일리 픽 종목이 아닙니다."),
    // 4번 에러 공석
    BUY_ORDER_LIMIT_PER_CATEGORY(ErrorDomain.TRADE_API.offset() + 5, "하루에 카테고리 당 하나의 매수 주문만 가능합니다."),
    BUY_API_REQUEST_FAILED(ErrorDomain.TRADE_API.offset() + 6, "매수 요청에 실패했습니다."),
    ORDER_PERMISSION_DENIED(ErrorDomain.TRADE_API.offset() + 7, "주문 요청 권한이 없습니다."),
    INVALID_ORDER_QUANTITY(ErrorDomain.TRADE_API.offset() + 8, "주문 수량이 유효하지 않습니다."),

    MATCH_API_ERROR(ErrorDomain.TRADE_API.offset() + 50, "Match Api 오류로 주문을 처리하지 못했습니다."),
    QUOTE_API_ERROR(ErrorDomain.TRADE_API.offset() + 51, "Quote Api 오류로 주문을 처리하지 못했습니다."),

    ORDER_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 90, "존재하지 않는 주문입니다."),

    // 포지션 도메인 (1000~1999)
    NOT_FOUND_USER_POSITION(ErrorDomain.TRADE_API.offset() + 1000, "유저가 소유한 포지션을 찾을 수 없습니다"),
    INVALID_POSITION_QUANTITY(ErrorDomain.TRADE_API.offset() + 1001, "거래 수량은 0일 수 없습니다"),
    POSITION_NOT_FOUND_FOR_SELL(ErrorDomain.TRADE_API.offset() + 1002, "보유 포지션 없이 매도할 수 없습니다"),
    INSUFFICIENT_POSITION_QUANTITY(ErrorDomain.TRADE_API.offset() + 1003, "보유 수량보다 많이 매도할 수 없습니다"),
    NOT_USER_POSITION(ErrorDomain.TRADE_API.offset() + 1004, "유저가 소유한 포지션이 아닙니다."),

    // 현금 도메인 (2000~2999)
    INVALID_CASH_AMOUNT(ErrorDomain.TRADE_API.offset() + 2000, "현금 금액이 올바르지 않습니다."),
    CASH_BALANCE_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 2001, "현금 잔액 정보를 찾을 수 없습니다."),
    INSUFFICIENT_CASH_BALANCE(ErrorDomain.TRADE_API.offset() + 2002, "가용 현금이 부족합니다."),
    CASH_HOLDING_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 2003, "해당 주문과 연결된 현금 홀딩이 없습니다."),
    CASH_HOLDING_ALREADY_FINALIZED(ErrorDomain.TRADE_API.offset() + 2004, "이미 처리된 현금 홀딩입니다."),

    // 환전 도메인 (3000~3999)
    EXCHANGE_RATE_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 3000, "환율 정보를 불러올 수 없습니다."),
    SAME_CURRENCY_EXCHANGE(ErrorDomain.TRADE_API.offset() + 3001, "같은 통화 간 환전은 불가합니다."),
    ;

    private final int code;
    private final String message;

    TradeErrorCode(int code, String message) {
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
