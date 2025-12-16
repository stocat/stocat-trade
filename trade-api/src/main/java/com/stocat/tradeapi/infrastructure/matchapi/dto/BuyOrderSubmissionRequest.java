package com.stocat.tradeapi.infrastructure.matchapi.dto;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuyOrderSubmissionRequest(
        Long orderId,
        Long memberId,
        OrderType orderType,
        Integer assetId,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif
) {
}
