package com.stocat.tradeapi.order.service.dto;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderDto(
        Long id,
        Long memberId,
        Integer assetId,
        TradeSide side,
        OrderType type,
        OrderStatus status,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getMemberId(),
                order.getAssetId(),
                order.getSide(),
                order.getType(),
                order.getStatus(),
                order.getQuantity(),
                order.getPrice(),
                order.getTif(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeletedAt()
        );
    }
}