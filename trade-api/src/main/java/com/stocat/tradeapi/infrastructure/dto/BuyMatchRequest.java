package com.stocat.tradeapi.infrastructure.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuyMatchRequest(
        Long memberId,
        Integer assetId,
        BigDecimal quantity,
        BigDecimal price
) {
}
