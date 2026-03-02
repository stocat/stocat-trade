package com.stocat.common.redis.dto;

import java.math.BigDecimal;

public record ExchangeRateLock(
        String fromCurrency,
        String toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal rate
) {
}