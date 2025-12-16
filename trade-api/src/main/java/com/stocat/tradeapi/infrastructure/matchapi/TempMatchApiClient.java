package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class TempMatchApiClient implements MatchApiClient {
    @Override
    public BuyOrderSubmissionResponse submitBuyOrder(OrderDto order) {
        throw new NotImplementedException();
    }

    @Override
    public void cancelOrder(Long orderId) {
        throw new NotImplementedException();
    }
}
