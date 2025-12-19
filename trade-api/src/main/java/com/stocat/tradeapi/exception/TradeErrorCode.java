package com.stocat.tradeapi.exception;

import com.stocat.common.exception.ErrorCode;
import com.stocat.common.exception.ErrorDomain;

public enum TradeErrorCode implements ErrorCode {
    INTERNAL_ERROR(ErrorDomain.TRADE_API.offset(), "서버 에러가 발생했습니다."),
    INVALID_ORDER_SIDE(ErrorDomain.TRADE_API.offset() + 1, "알 수 없는 주문 요청입니다."),
    ASSET_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 2, "존재하지 않는 종목입니다."),
    NOT_DAILY_PICK_ASSET(ErrorDomain.TRADE_API.offset() + 3, "데일리 픽 종목이 아닙니다."),
    PENDING_ORDER_EXISTS_IN_CATEGORY(ErrorDomain.TRADE_API.offset() + 4, "해당 카테고리에 매수 대기 중인 종목이 있습니다."),
    EXECUTED_TODAY_ORDER_EXISTS_IN_CATEGORY(ErrorDomain.TRADE_API.offset() + 5, "해당 카테고리에 오늘 이미 체결된 거래가 있습니다."),
    BUY_API_REQUEST_FAILED(ErrorDomain.TRADE_API.offset() + 6, "매수 요청에 실패했습니다."),
    ORDER_PERMISSION_DENIED(ErrorDomain.TRADE_API.offset() + 7, "주문 요청 권한이 없습니다."),
    INVALID_ORDER_QUANTITY(ErrorDomain.TRADE_API.offset() + 8, "주문 수량이 유효하지 않습니다."),

    MATCH_API_ERROR(ErrorDomain.TRADE_API.offset() + 50, "Match Api 오류로 주문을 처리하지 못했습니다."),
    QUOTE_API_ERROR(ErrorDomain.TRADE_API.offset() + 51, "Quote Api 오류로 주문을 처리하지 못했습니다."),

    ORDER_NOT_FOUND(ErrorDomain.TRADE_API.offset() + 90, "존재하지 않는 주문입니다."),
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
