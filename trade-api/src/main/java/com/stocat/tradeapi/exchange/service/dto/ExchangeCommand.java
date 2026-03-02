package com.stocat.tradeapi.exchange.service.dto;

import com.stocat.common.domain.Currency;

import java.math.BigDecimal;

public record ExchangeCommand(
        Long userId,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount
) {
}