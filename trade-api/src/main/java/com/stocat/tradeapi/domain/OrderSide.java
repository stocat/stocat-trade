package com.stocat.tradeapi.domain;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;

/**
 * 매매 구분
 */
// TODO: Asset Scraper와 통합 여부
public enum OrderSide {
    BUY,
    SELL;

    public static OrderSide fromUpbitSide(String side) {
        if (side.equals("ASK")) {
            return SELL;
        } else if (side.equals("BID")) {
            return BUY;
        }

        throw new ApiException(TradeErrorCode.INTERNAL_ERROR);
    }
}
