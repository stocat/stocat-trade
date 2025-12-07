package com.stocat.tradeapi.infrastructure;

import com.stocat.tradeapi.infrastructure.dto.AssetDto;

public interface QuoteApiClient {
    AssetDto fetchAsset(String ticker);
    AssetDto fetchAsset(int assetId);
}
