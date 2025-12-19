package com.stocat.tradeapi.order.service.dto.command;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BuyOrderCommand(
        Long memberId,
        OrderType orderType,
        AssetDto asset,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif,
        LocalDateTime requestTime
) {
}
