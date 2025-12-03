package com.stocat.tradeapi.service.dto.command;

import com.stocat.common.domain.asset.domain.AssetsCategory;

import java.math.BigDecimal;

public record BuyOrderCommand(
        Long memberId,
        String ticker,
        BigDecimal quantity,
        BigDecimal price,
        AssetsCategory category
) {
}
