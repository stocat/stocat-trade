package com.stocat.tradeapi.position.service.dto.command;

import com.stocat.tradeapi.position.controller.dto.NewPositionRequest;

import java.math.BigDecimal;

public record NewPositionCommand(
        Long userId,
        Long assetId,
        BigDecimal quantity,
        BigDecimal avgEntryPrice
) {
    public static NewPositionCommand from(NewPositionRequest request) {
        return new NewPositionCommand(
                request.getUserId(),
                request.getAssetId(),
                request.getQuantity(),
                request.getAvgEntryPrice()
        );
    }
}
