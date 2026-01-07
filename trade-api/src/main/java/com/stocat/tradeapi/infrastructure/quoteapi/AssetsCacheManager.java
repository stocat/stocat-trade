package com.stocat.tradeapi.infrastructure.quoteapi;

import com.stocat.common.exception.ApiException;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.config.CacheConfig;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetsResponse;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.IndexedAssetsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AssetsCacheManager {
    private final RestTemplate assetApiRestTemplate;

    @Cacheable(value = CacheConfig.ASSETS)
    public IndexedAssetsResponse getActiveAssets() {
        ResponseEntity<ApiResponse<AssetsResponse>> apiResponse = assetApiRestTemplate.exchange(
                "/internal/assets",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<AssetsResponse>>() {
                });

        if (!apiResponse.getStatusCode().is2xxSuccessful()) {
            throw new ApiException(TradeErrorCode.QUOTE_API_ERROR);
        }

        if (!apiResponse.getStatusCode().is2xxSuccessful()) {
            throw new ApiException(TradeErrorCode.QUOTE_API_ERROR);
        }

        AssetsResponse assetsResponse = apiResponse.getBody().data();
        return IndexedAssetsResponse.from(assetsResponse);
    }

    @CacheEvict(value = CacheConfig.ASSETS)
    public void refreshActiveAssets() {
    }
}
