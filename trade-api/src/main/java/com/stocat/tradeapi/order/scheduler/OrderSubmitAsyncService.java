package com.stocat.tradeapi.order.scheduler;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyRequest;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyResult;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSubmitAsyncService {
    private final MatchApiClient matchApiClient;
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void submitOrderAsync(Order order) {
        order = orderQueryService.findByIdForUpdate(order.getId());

        if (order.getStatus() != OrderStatus.CREATED) {
            log.warn("매수 주문의 상태가 CREATED가 아닙니다. orderId: {}, status: {}", order.getId(), order.getStatus());
            return;
        }

        MatchBuyRequest request = MatchBuyRequest.builder()
                .memberId(order.getMemberId())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .build();

        MatchBuyResult result = matchApiClient.buy(request);

        if (result.isSuccess()) {
            orderCommandService.updateOrderStatus(order, OrderStatus.PENDING);
        }
        if (result.isRejected()) {
            orderCommandService.updateOrderStatus(order, OrderStatus.REJECTED);
        }
    }
}