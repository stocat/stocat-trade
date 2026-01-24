package com.stocat.tradeapi.position.service.dto.command;

import com.stocat.tradeapi.position.controller.dto.PositionUpsertRequest;

import java.math.BigDecimal;

public record PositionUpsertCommand(
        Long userId,
        Long assetId,
        BigDecimal quantity,
        BigDecimal avgEntryPrice
) {
    public static PositionUpsertCommand from(PositionUpsertRequest request) {
        return new PositionUpsertCommand(
                request.getUserId(),
                request.getAssetId(),
                request.getQuantity(),
                request.getAvgEntryPrice()
        );
    }
}
