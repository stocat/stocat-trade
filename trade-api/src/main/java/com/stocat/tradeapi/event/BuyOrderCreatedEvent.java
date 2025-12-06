package com.stocat.tradeapi.event;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuyOrderCreatedEvent(
        Long orderId,
        Long memberId,
        Integer assetId,
        TradeSide side,
        Currency currency,
        OrderStatus status,
        BigDecimal quantity,
        BigDecimal price,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static BuyOrderCreatedEvent from(Order order) {
        return new BuyOrderCreatedEvent(
                order.getId(),
                order.getMemberId(),
                order.getAssetId(),
                order.getSide(),
                order.getCurrency(),
                order.getStatus(),
                order.getQuantity(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt()
        );
    }
}
