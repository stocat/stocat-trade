package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class TempMatchApiClient implements MatchApiClient {
    @Override
    public BuyOrderSubmissionResponse submitBuyOrder(BuyOrderSubmissionRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public SellOrderSubmissionResponse submitSellOrder(SellOrderSubmissionRequest request) {
        throw new NotImplementedException();
    }

    @Override
    public void cancelOrder(Long orderId) {
        throw new NotImplementedException();
    }
}
