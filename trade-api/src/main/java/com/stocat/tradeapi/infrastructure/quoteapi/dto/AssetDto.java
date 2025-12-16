package com.stocat.tradeapi.infrastructure.quoteapi.dto;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import lombok.Builder;

@Builder
public record AssetDto(
        Integer id,
        String symbol,
        AssetsCategory category,
        Currency currency,
        Boolean isActive,
        Boolean isDaily,
        String koName,
        String usName
) {
}
