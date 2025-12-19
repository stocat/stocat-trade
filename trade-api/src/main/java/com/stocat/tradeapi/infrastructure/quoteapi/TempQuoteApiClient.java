package com.stocat.tradeapi.infrastructure.quoteapi;

import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class TempQuoteApiClient implements QuoteApiClient {
    @Override
    public AssetDto fetchAsset(String symbol) {
        throw new NotImplementedException();
    }
}
