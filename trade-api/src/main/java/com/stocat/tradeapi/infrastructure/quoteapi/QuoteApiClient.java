package com.stocat.tradeapi.infrastructure.quoteapi;

import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;

public interface QuoteApiClient {
    AssetDto fetchAsset(String symbol);
}
