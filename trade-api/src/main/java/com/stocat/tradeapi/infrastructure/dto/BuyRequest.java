package com.stocat.tradeapi.infrastructure.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuyRequest(
        Long memberId,
        String ticker,
        BigDecimal quantity,
        BigDecimal price
) {
}
