package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionResponse;

public interface MatchApiClient {
    BuyOrderSubmissionResponse submitBuyOrder(BuyOrderSubmissionRequest request);
    SellOrderSubmissionResponse submitSellOrder(SellOrderSubmissionRequest request);
    void cancelOrder(Long orderId);
}
