package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final QuoteApiClient quoteApiClient;
    private final MatchApiClient matchApiClient;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command, AssetDto asset, Long cashHoldingId) {
        validateBuyOrder(command, asset);

        Order order = orderCommandService.createBuyOrder(command, asset, cashHoldingId);
        OrderDto dto = OrderDto.from(order);

        matchApiClient.submitBuyOrder(BuyOrderSubmissionRequest.from(order));

        return dto;
    }


    private void validateBuyOrder(BuyOrderCommand command, AssetDto asset) {
        if (!isValidBuyOrderQuantity(command, asset)) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_QUANTITY);
        }

        //TODO: 장 마감시간 검증

        if (existsTodayBuyOrderInCategory(command, asset)) {
            throw new ApiException(TradeErrorCode.BUY_ORDER_LIMIT_PER_CATEGORY);
        }
    }

    private boolean isValidBuyOrderQuantity(BuyOrderCommand command, AssetDto asset) {
        if (command.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (asset.category() == AssetsCategory.CRYPTO) {
            return command.quantity().scale() <= 4;
        } else {
            return command.quantity().scale() == 0;
        }
    }

    private boolean existsTodayBuyOrderInCategory(BuyOrderCommand command, AssetDto asset) {
        List<Order> orders = orderQueryService.findUserBuyOrdersToday(command.userId(), command.requestTime());

        return orders.stream()
                .filter(previousOrder -> previousOrder.getStatus() != OrderStatus.CANCELED)
                .anyMatch(previousOrder -> {
                    AssetDto previousOrderAsset = quoteApiClient.fetchAssetById(previousOrder.getAssetId());
                    return asset.category() == previousOrderAsset.category();
                });
    }


    @Transactional
    public OrderDto cancelOrder(OrderCancelCommand command) {
        Order order = orderQueryService.findByIdForUpdate(command.orderId());

        if (!order.getUserId().equals(command.userId())) {
            throw new ApiException(TradeErrorCode.ORDER_PERMISSION_DENIED);
        }

        matchApiClient.cancelOrder(command.orderId());

        order = orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);
        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto fillBuyOrder(FillBuyOrderCommand command) {
        Order order = orderQueryService.findByIdForUpdate(command.orderId());
        // TODO: 부분 체결이 있다면, 부분체결 상황을 알기 위한 업데이트 추가
        // TODO: 부분 체결을 위한 추가 테이블 혹은 필드가 있다면 추가
        orderCommandService.updateOrderStatus(order, OrderStatus.FILLED);

        return OrderDto.from(order);
    }


    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
