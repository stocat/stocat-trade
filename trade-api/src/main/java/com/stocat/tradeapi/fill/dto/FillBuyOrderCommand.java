package com.stocat.tradeapi.fill.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FillBuyOrderCommand(
        Long orderId,
        Long assetId,
        TradeSide side,
        BigDecimal quantity,
        BigDecimal price,
        Currency priceCurrency,
        BigDecimal feeAmount,
        Currency feeCurrency,
        LocalDateTime executedAt,
        Long exchangeRateId,
        BigDecimal exchangeRate,
        String pair
) {
}
