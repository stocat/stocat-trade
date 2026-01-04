package com.stocat.tradeapi.order.service.dto.command;

public record OrderCancelCommand(
        Long orderId,
        Long userId
) {
}
