package com.stocat.tradeapi.infrastructure;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.infrastructure.dto.BuyMatchRequest;

public interface MatchApiClient {
    ApiResponse<?> buy(BuyMatchRequest request);
    ApiResponse<?> cancelOrder(Long orderId);
}
