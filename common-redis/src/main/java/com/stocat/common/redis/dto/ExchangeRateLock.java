package com.stocat.common.redis.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ExchangeRateLock(
        Long userId,
        String fromCurrency,
        String toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal rate
) {
}