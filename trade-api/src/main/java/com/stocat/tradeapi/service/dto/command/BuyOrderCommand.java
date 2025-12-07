package com.stocat.tradeapi.service.dto.command;

import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuyOrderCommand(
        Long memberId,
        OrderType orderType,
        AssetDto asset,
        BigDecimal quantity,
        BigDecimal price
) {
}
