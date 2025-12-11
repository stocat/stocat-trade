package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.order.event.BuyOrderCreatedEvent;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final MatchApiClient matchApiClient;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        validateBuyOrder(command);

        Order order = orderCommandService.createBuyOrder(command);

        BuyOrderCreatedEvent event = BuyOrderCreatedEvent.from(order);
        eventPublisher.publishEvent(event);

        // TODO: 사용 가능 포인트(현금) 감소 로직 추가

        return OrderDto.from(order);
    }


    public void validateBuyOrder(BuyOrderCommand command) {
        AssetDto asset = command.asset();

        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }

        //TODO: 장 마감시간 검증

        if (orderQueryService.existsPendingBuyOrdersInCategory(command.memberId(), asset.category())) {
            throw new ApiException(TradeErrorCode.PENDING_ORDER_EXISTS_IN_CATEGORY);
        }
        if (orderQueryService.existsTodayExecutedBuyOrdersInCategory(command.memberId(), asset.category(), command.requestTime())) {
            throw new ApiException(TradeErrorCode.EXECUTED_TODAY_ORDER_EXISTS_IN_CATEGORY);
        }
    }

    @Transactional
    public OrderDto cancelOrder(OrderCancelCommand command) {
        Order order = orderQueryService.findByIdForUpdate(command.orderId());

        if (!order.getMemberId().equals(command.memberId())) {
            throw new ApiException(TradeErrorCode.ORDER_PERMISSION_DENIED);
        }

        matchApiClient.cancelOrder(command.orderId());

        order = orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);
        return OrderDto.from(order);
    }

    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
