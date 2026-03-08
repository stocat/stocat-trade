package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.order.event.OrderCanceledEvent;
import com.stocat.tradeapi.order.service.OrderQueryService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelOrderUsecase {
    private final OrderQueryService orderQueryService;
    private final SellOrderFacade sellOrderFacade;
    private final BuyOrderFacade buyOrderFacade;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 취소 요청 처리
     * <p>
     * 1. 주문 조회 및 소유자/상태 검증을 수행합니다. 2. 주문 타입(매수/매도)에 따라 적절한 Facade를 호출하여 내부 상태를 변경합니다. 3. 트랜잭션 커밋 후 외부 매칭 엔진에 취소 요청을 보내기
     * 위해 이벤트를 발행합니다.
     * </p>
     *
     * @param command 주문 취소 요청 커맨드
     * @return 취소된 주문 정보
     */
    @Transactional
    public OrderDto cancelOrder(OrderCancelCommand command) {
        // 1. 주문 검증
        Order order = orderQueryService.findById(command.orderId());
        validateCancelOrder(command.userId(), order.getUserId(), order.getStatus());

        // 2. 내부 상태 변경 (DB 반영 우선)
        OrderDto canceledOrder = cancelInternalOrder(order);

        // 3. 트랜잭션 커밋 후 외부 API 호출을 위해 이벤트 발행
        eventPublisher.publishEvent(new OrderCanceledEvent(canceledOrder));

        return canceledOrder;
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

    private OrderDto cancelInternalOrder(Order order) {
        if (order.getSide() == TradeSide.SELL) {
            return sellOrderFacade.cancelSellOrder(order);
        }
        if (order.getSide() == TradeSide.BUY) {
            return buyOrderFacade.cancelBuyOrder(order);
        }
        throw new ApiException(TradeErrorCode.INVALID_ORDER_SIDE);
    }
}
