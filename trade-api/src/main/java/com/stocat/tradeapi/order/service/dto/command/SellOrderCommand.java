package com.stocat.tradeapi.order.service.dto.command;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SellOrderCommand(
        Long userId,
        String assetSymbol,
        OrderType orderType,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif,
        LocalDateTime requestTime
) {
}
