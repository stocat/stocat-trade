package com.stocat.tradeapi.infrastructure.matchapi.dto;

import com.stocat.common.domain.order.Order;
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
    public static BuyOrderSubmissionRequest from(Order order) {
        return BuyOrderSubmissionRequest.builder()
                .orderId(order.getId())
                .memberId(order.getMemberId())
                .orderType(order.getType())
                .assetId(order.getAssetId())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .tif(order.getTif())
                .build();
    };
}
