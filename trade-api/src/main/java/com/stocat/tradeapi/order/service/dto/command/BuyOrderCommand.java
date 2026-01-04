package com.stocat.tradeapi.order.service.dto.command;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BuyOrderCommand(
        Long userId,
        String assetSymbol,
        OrderType orderType,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif,
        LocalDateTime requestTime
) {
}
