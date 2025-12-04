package com.stocat.tradeapi.infrastructure;

import com.stocat.tradeapi.infrastructure.dto.AssetDto;

public interface QuoteApiClient {
    // 인메모리 캐시로 구현
    AssetDto fetchAsset(String ticker);
    AssetDto fetchAsset(int assetId);
}
