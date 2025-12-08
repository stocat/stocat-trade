package com.stocat.tradeapi.infrastructure.dto;

import com.stocat.common.domain.order.OrderType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MatchBuyRequest(
        Long memberId,
        OrderType orderType,
        Integer assetId,
        BigDecimal quantity,
        BigDecimal price
) {
}
