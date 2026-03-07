package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiFacade;
import com.stocat.tradeapi.order.event.OrderCanceledEvent;
import com.stocat.tradeapi.order.service.OrderQueryService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CancelOrderUsecase {
    private final OrderQueryService orderQueryService;
    private final SellOrderFacade sellOrderFacade;
    private final MatchApiFacade matchApiFacade;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto cancelOrder(OrderCancelCommand command) {
        // 1. 주문 검증
        Order order = orderQueryService.findById(command.orderId());
        validateCancelOrder(command.userId(), order.getUserId(), order.getStatus());

        // 2. 내부 상태 변경 (DB 반영 우선)
        OrderDto canceledOrder = cancelInternalOrder(command.orderId(), command.userId(), order.getSide());

        // 3. 트랜잭션 커밋 후 외부 API 호출을 위해 이벤트 발행
        eventPublisher.publishEvent(new OrderCanceledEvent(canceledOrder));

        return canceledOrder;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancellation(OrderCanceledEvent event) {
        matchApiFacade.submitCancelOrderWithRetry(event);
    }

    private void validateCancelOrder(Long userId, Long orderUserId, OrderStatus status) {
        if (!orderUserId.equals(userId)) {
            throw new ApiException(TradeErrorCode.ORDER_NOT_FOUND);
        }

        // 이미 완료된 주문이거나 취소된 주문인 경우 예외 발생
        if (status != OrderStatus.PENDING) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_STATUS);
        }
    }

    private OrderDto cancelInternalOrder(Long orderId, Long userId, TradeSide side) {
        if (side == TradeSide.SELL) {
            return sellOrderFacade.cancelSellOrder(orderId, userId);
        }
        if (side == TradeSide.BUY) {
//            TODO: 매수 주문 취소 로직 구현
        }
        throw new ApiException(TradeErrorCode.INVALID_ORDER_SIDE);
    }
}
