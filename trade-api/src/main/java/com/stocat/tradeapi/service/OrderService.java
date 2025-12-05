package com.stocat.tradeapi.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.exception.ApiException;
import com.stocat.common.response.ApiResponse;
import com.stocat.common.domain.order.Order;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.BuyMatchRequest;
import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServicePort {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final MatchApiClient matchApiClient;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        LocalDateTime requestTime = LocalDateTime.now();
        validateBuyOrder(command, requestTime);

        Order order = orderCommandService.createBuyOrder(command);

        BuyMatchRequest request = BuyMatchRequest.builder()
                .memberId(command.memberId())
                .ticker(command.asset().ticker())
                .quantity(command.quantity())
                .price(command.price())
                .build();

        ApiResponse<?> response = matchApiClient.buy(request);
        if (response.code() != ApiResponse.SUCCESS_CODE) {
            throw new ApiException(TradeErrorCode.BUY_API_REQUEST_FAILED);
        }

        return OrderDto.from(order);
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

    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
