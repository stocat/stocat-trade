package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class TempMatchApiClient implements MatchApiClient {
    @Override
    public BuyOrderSubmissionResponse submitBuyOrder(BuyOrderSubmissionRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public void cancelOrder(Long orderId) {
        throw new NotImplementedException();
    }
}
