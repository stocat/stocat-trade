package com.stocat.tradeapi.order.usecase;

import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelOrderUsecase {
    private final OrderService orderService;
    private final MatchApiClient matchApiClient;

    public OrderDto cancelOrder(OrderCancelCommand command) {
        // 1. 외부 체결 엔진에 취소 요청
        matchApiClient.cancelOrder(command.orderId());

        // 2. DB 상태 업데이트
        return orderService.cancelOrder(command);
    }
}
