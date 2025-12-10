package com.stocat.tradeapi.service.dto.command;

public record GetPositionCommand(
        Long positionId,
        Long userId
) {
}
