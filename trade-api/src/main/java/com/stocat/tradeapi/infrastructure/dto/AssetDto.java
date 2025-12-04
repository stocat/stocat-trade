package com.stocat.tradeapi.infrastructure.dto;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;

public record AssetDto(
        Integer id,
        String symbol,
        String ticker,
        AssetsCategory category,
        Currency currency,
        Boolean isActive,
        String koName,
        String usName,
        Boolean isDaily
) {
}
