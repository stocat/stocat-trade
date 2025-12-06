package com.stocat.tradeapi.service.dto.command;

import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuyOrderCommand(
        Long memberId,
        AssetDto asset,
        BigDecimal quantity,
        BigDecimal price
) {
}
