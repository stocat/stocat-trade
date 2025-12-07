package com.stocat.tradeapi.service.dto;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderDto(
        Long id,
        Long memberId,
        Integer assetId,
        AssetsCategory assetsCategory,
        TradeSide side,
        OrderType type,
        Currency currency,
        OrderStatus status,
        BigDecimal quantity,
        BigDecimal price,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getMemberId(),
                order.getAssetId(),
                order.getCategory(),
                order.getSide(),
                order.getType(),
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