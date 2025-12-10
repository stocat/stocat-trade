package com.stocat.tradeapi.service.dto.command;

public record GetUserPositionCommand(
        Long userId
) {
    public static GetUserPositionCommand from(Long userId) {
        return new GetUserPositionCommand(userId);
    }
}
