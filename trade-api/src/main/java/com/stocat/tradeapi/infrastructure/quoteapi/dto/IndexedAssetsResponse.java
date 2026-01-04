package com.stocat.tradeapi.infrastructure.quoteapi.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record IndexedAssetsResponse(
        LocalDate date,
        Map<Long, AssetDto> assetsById,
        Map<String, AssetDto> assetsBySymbol
) {
    public static IndexedAssetsResponse from(AssetsResponse response) {
        return new IndexedAssetsResponse(
                response.date(),
                response.assets().stream().collect(Collectors.toMap(
                        AssetDto::id, Function.identity())),
                response.assets().stream().collect(Collectors.toMap(
                        AssetDto::symbol, Function.identity()))
        );
    }
}
