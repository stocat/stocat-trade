package com.stocat.tradeapi.infrastructure.quoteapi;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.IndexedAssetsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DefaultQuoteApiClient implements QuoteApiClient {
    private static final int ASSET_INIT_HOUR = 9;

    private final AssetsCacheManager assetsCacheManager;
    private final Clock clock;

    @Override
    public AssetDto fetchAsset(String symbol) {
        IndexedAssetsResponse response = assetsCacheManager.getActiveAssets();

        LocalDateTime now = LocalDateTime.now(clock);

        if (now.getHour() >= ASSET_INIT_HOUR && now.toLocalDate().isAfter(response.date())) {
            assetsCacheManager.refreshActiveAssets();
        }

        response = assetsCacheManager.getActiveAssets();

        AssetDto assetDto = response.assetsBySymbol().get(symbol);
        if (assetDto == null) {
            throw new ApiException(TradeErrorCode.ASSET_NOT_FOUND);
        }
        return assetDto;
    }

    @Override
    public Integer fetchCashAssetId(AssetsCategory category) {
        return fetchAsset(category.name()).id();
    }
}
