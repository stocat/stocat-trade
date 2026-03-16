package com.stocat.tradeapi.infrastructure.messaging;

import com.stocat.common.domain.TradeSide;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock Consumer: 큐에서 메시지를 소비하여 실제 API를 호출하는 역할을 시뮬레이션합니다. 나중에 실제 RabbitMQ/SQS 리스너로 대체될 수 있습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MockMatchApiConsumer {

    private final MatchApiClient matchApiClient;

    /**
     * 주문 메시지를 소비하여 매수 또는 매도 유형에 맞게 매칭 엔진 API를 호출합니다.
     *
     * @param orderDto 소비된 주문 데이터 전송 객체
     */
    public void consumeOrder(OrderDto orderDto) {
        log.info("[Mock Consumer] 주문 메시지 소비, 매칭 엔진 API 호출 시작: orderId={}", orderDto.id());
        if (orderDto.side() == TradeSide.BUY) {
            matchApiClient.submitBuyOrder(BuyOrderSubmissionRequest.from(orderDto));
        } else if (orderDto.side() == TradeSide.SELL) {
            matchApiClient.submitSellOrder(SellOrderSubmissionRequest.from(orderDto));
        }
    }

    /**
     * 주문 취소 메시지를 소비하여 해당 주문의 취소 요청을 매칭 엔진 API로 전송합니다.
     *
     * @param orderId 소비된 취소할 주문의 ID
     */
    public void consumeCancelOrder(Long orderId) {
        log.info("[Mock Consumer] 주문 취소 메시지 소비, 매칭 엔진 API 호출 시작: orderId={}", orderId);
        matchApiClient.submitCancelOrder(orderId);
    }
}
