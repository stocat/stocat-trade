package com.stocat.tradeapi.position.service.dto.command;

public record GetUserPositionCommand(
        Long userId
) {
    public static GetUserPositionCommand from(Long userId) {
        return new GetUserPositionCommand(userId);
    }
}
