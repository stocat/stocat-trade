package com.stocat.tradeapi.infrastructure.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MatchBuyRequest(
        Long memberId,
        Integer assetId,
        BigDecimal quantity,
        BigDecimal price
) {
}
