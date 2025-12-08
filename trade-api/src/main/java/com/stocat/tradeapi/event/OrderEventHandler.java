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
