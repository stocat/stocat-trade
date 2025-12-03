package com.stocat.tradeapi.infrastructure;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.infrastructure.dto.BuyRequest;

public interface MatchApiClient {
    ApiResponse<?> buy(BuyRequest request);
}
