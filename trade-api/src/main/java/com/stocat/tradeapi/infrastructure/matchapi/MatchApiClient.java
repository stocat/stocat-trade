package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.order.service.dto.OrderDto;

public interface MatchApiClient {
    BuyOrderSubmissionResponse submitBuyOrder(OrderDto order);
    void cancelOrder(Long orderId);
}
