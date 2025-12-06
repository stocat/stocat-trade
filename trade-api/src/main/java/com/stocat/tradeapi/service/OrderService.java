package com.stocat.tradeapi.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.OrderRepository;
import com.stocat.common.response.ApiResponse;
import com.stocat.common.domain.order.Order;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.ApiResponseCode;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.BuyMatchRequest;
import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService implements OrderServicePort {


    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final MatchApiClient matchApiClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        LocalDateTime requestTime = LocalDateTime.now();
        validateBuyOrder(command, requestTime);

        Order order = orderCommandService.createBuyOrder(command);

        OrderDto orderDto = OrderDto.from(order);
        eventPublisher.publishEvent(order);

        // TODO: 사용 가능 포인트(현금) 감소 로직 추가

        return orderDto;
    }

    public void validateBuyOrder(BuyOrderCommand command, LocalDateTime requestTime) {
        AssetDto asset = command.asset();

        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
        if (existsPendingOrderInCategory(command.memberId(), asset.category())) {
            throw new ApiException(TradeErrorCode.PENDING_ORDER_EXISTS_IN_CATEGORY);
        }
        if (existsTodayExecutedOrderInCategory(command.memberId(), asset.category(), requestTime)) {
            throw new ApiException(TradeErrorCode.EXECUTED_TODAY_ORDER_EXISTS_IN_CATEGORY);
        }
    }

    private boolean existsPendingOrderInCategory(Long memberId, AssetsCategory category) {
        List<Order> orders = orderQueryService.findPendingBuyOrdersInCategory(memberId, category);

        return orders.stream().anyMatch(
                pendingOrder -> category.equals(pendingOrder.getCategory()));
    }

    private boolean existsTodayExecutedOrderInCategory(Long memberId, AssetsCategory category, LocalDateTime requestTime) {
        List<Order> orders = orderQueryService.findTodayExecutedBuyOrdersInCategory(memberId, category, requestTime);

        return orders.stream().anyMatch(
                excutedOrder -> category.equals(excutedOrder.getCategory()));
    }

    @Transactional
    public OrderDto cancelOrder(OrderCancelCommand command) {
        Order order = orderQueryService.findById(command.orderId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.ORDER_NOT_FOUND));

        if (!order.getMemberId().equals(command.memberId())) {
            throw new ApiException(TradeErrorCode.ORDER_PERMISSION_DENIED);
        }

        ApiResponse<?> apiResponse = matchApiClient.cancelOrder(command.orderId());

        if (apiResponse.code() == ApiResponseCode.ORDER_ALREADY_CANCELED) {
            log.warn("거래소에서는 이미 취소된 거래건 입니다. order id: {}", command.orderId());
        } else if (apiResponse.code() != ApiResponse.SUCCESS_CODE) {
            throw new ApiException(TradeErrorCode.MATCHING_ENGINE_ERROR);
        }

        order = orderCommandService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
        return OrderDto.from(order);
    }

    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
