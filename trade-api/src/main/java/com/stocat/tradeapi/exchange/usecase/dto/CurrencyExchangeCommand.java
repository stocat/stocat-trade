package com.stocat.tradeapi.exchange.usecase.dto;

import com.stocat.common.domain.Currency;

import java.math.BigDecimal;

public record CurrencyExchangeCommand(
        Long userId,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal fromAmount
) {
}