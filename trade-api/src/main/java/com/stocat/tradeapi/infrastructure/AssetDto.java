package com.stocat.tradeapi.infrastructure;

import com.stocat.common.domain.asset.domain.AssetsCategory;
import com.stocat.common.domain.asset.domain.Currency;

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
