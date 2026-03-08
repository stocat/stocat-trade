package com.stocat.tradeapi.exchange.service.dto;

import com.stocat.common.domain.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExchangeCommand(
        Long userId,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount
) {
}