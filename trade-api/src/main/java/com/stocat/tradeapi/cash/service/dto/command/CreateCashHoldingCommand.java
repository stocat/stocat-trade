package com.stocat.tradeapi.cash.service.dto.command;

import com.stocat.common.domain.Currency;
import java.math.BigDecimal;

public record CreateCashHoldingCommand(
        Long userId,
        Currency currency,
        Long orderId,
        BigDecimal amount
) {
}
