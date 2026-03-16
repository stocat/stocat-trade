package com.stocat.tradeapi.infrastructure.matchapi;

import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@Profile("local")
public class MockMatchApiClient implements MatchApiClient {

    @Override
    public BuyOrderSubmissionResponse submitBuyOrder(BuyOrderSubmissionRequest request) {
        log.info("[Mock Match API] 매수 주문 API 호출됨: {}", request);
        return null;
    }

    @Override
    public SellOrderSubmissionResponse submitSellOrder(SellOrderSubmissionRequest request) {
        log.info("[Mock Match API] 매도 주문 API 호출됨: {}", request);
        return null;
    }

    @Override
    public void submitCancelOrder(Long orderId) {
        log.info("[Mock Match API] 주문 취소 API 호출됨: orderId={}", orderId);
    }
}