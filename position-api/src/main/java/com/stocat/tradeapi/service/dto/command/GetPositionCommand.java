package com.stocat.tradeapi.service.dto.command;

public record GetPositionCommand(
        Long positionId,
        Long userId
) {
    public static GetPositionCommand from(Long positionId, Long userId) {
        return new GetPositionCommand(positionId, userId);
    }
}
