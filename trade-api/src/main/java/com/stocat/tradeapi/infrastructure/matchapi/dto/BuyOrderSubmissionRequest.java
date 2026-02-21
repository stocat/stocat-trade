package com.stocat.tradeapi.infrastructure.matchapi.dto;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record BuyOrderSubmissionRequest(
        Long orderId,
        Long userId,
        OrderType orderType,
        Long assetId,
        BigDecimal quantity,
        BigDecimal price,
        OrderTif tif
) {
    public static BuyOrderSubmissionRequest from(OrderDto orderDto) {
        return BuyOrderSubmissionRequest.builder()
                .orderId(orderDto.id())
                .userId(orderDto.userId())
                .orderType(orderDto.type())
                .assetId(orderDto.assetId())
                .quantity(orderDto.quantity())
                .price(orderDto.price())
                .tif(orderDto.tif())
                .build();
    }
}
