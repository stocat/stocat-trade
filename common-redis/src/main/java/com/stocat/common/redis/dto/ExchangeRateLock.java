package com.stocat.common.redis.dto;

import java.math.BigDecimal;

public record ExchangeRateLock(
        Long userId,
        String fromCurrency,
        String toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal rate
) {
}