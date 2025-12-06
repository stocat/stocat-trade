package com.stocat.tradeapi.service.dto.command;

public record OrderCancelCommand(
        Long orderId,
        Long memberId
) {
}
