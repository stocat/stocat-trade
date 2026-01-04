package com.stocat.tradeapi.infrastructure.quoteapi;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;

public interface QuoteApiClient {
    AssetDto fetchAsset(String symbol);
    AssetDto fetchAssetById(Long assetId);
    Long fetchCashAssetId(AssetsCategory category);
}
