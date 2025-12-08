package com.stocat.tradeapi.infrastructure;

import com.stocat.tradeapi.infrastructure.dto.MatchBuyRequest;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyResult;

public interface MatchApiClient {
    MatchBuyResult buy(MatchBuyRequest request);
    void cancelOrder(Long orderId);
}
