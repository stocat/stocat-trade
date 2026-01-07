package com.stocat.tradeapi.infrastructure.quoteapi.dto;

import java.time.LocalDate;
import java.util.List;

public record AssetsResponse(
        LocalDate date,
        List<AssetDto> assets
) {
}
