package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;

public interface MatchApiClient {
    BuyOrderSubmissionResponse submitBuyOrder(BuyOrderSubmissionRequest request);
    void cancelOrder(Long orderId);
}
