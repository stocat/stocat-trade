package com.stocat.tradeapi.event;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyRequest;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyResult;
import com.stocat.tradeapi.service.OrderCommandService;
import com.stocat.tradeapi.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {
    // TODO : 거절, 실패 코드 혹은 데이터 명세
    private static final int REJECT_CODE = 5000;

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final MatchApiClient matchApiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleBuyOrderCreatedEvent(BuyOrderCreatedEvent event) {
        MatchBuyRequest request = MatchBuyRequest.builder()
                .memberId(event.memberId())
                .quantity(event.quantity())
                .price(event.price())
                .build();

        MatchBuyResult result = matchApiClient.buy(request);

        if (result.isFailure()) {
            log.warn("매수 요청 중 오류 발생 {}", result);
            return;
        }

        Order order = orderQueryService.findById(event.orderId());

        if (result.isSuccess()) {
            orderCommandService.updateOrderStatus(order, OrderStatus.PENDING);
            return;
        }

        if (result.isRejected()) {
            orderCommandService.updateOrderStatus(order, OrderStatus.REJECTED);
            return;
        }
    }
}
