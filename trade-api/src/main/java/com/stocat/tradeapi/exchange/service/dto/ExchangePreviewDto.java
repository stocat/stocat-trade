package com.stocat.tradeapi.exchange.service.dto;

import java.math.BigDecimal;

public record ExchangePreviewDto(
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal exchangeRate,
        String rateLockKey,
        long expiresIn
) {
}