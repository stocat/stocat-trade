package com.stocat.tradeapi.service;

import com.stocat.common.domain.asset.domain.AssetsCategory;
import com.stocat.common.exception.ApiException;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.domain.Order;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.AssetDto;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.dto.BuyRequest;
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
public class OrderService {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    private final QuoteApiClient quoteApiClient;
    private final MatchApiClient matchApiClient;



    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command, LocalDateTime requestTime) {
        validateBuyOrder(command, requestTime);

        AssetDto asset = quoteApiClient.fetchAsset(command.ticker());
        Order order =  orderCommandService.createBuyOrder(command, asset);

        BuyRequest request = BuyRequest.builder()
                .memberId(command.memberId())
                .ticker(command.ticker())
                .quantity(command.quantity())
                .price(command.price())
                .build();

        ApiResponse<?> response = matchApiClient.buy(request);
        if(response.code() != ApiResponse.SUCCESS_CODE) {
            throw new ApiException(TradeErrorCode.BUY_API_REQUEST_FAILED);
        }

        return OrderDto.from(order);
    }

    private void validateBuyOrder(BuyOrderCommand command, LocalDateTime requestTime) {
        AssetDto asset = quoteApiClient.fetchAsset(command.ticker());

        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
        if (existsPendingOrderInCategory(command.memberId(), asset.category())) {
            throw new ApiException(TradeErrorCode.PENDING_ORDER_EXISTS_IN_CATEGORY);
        }
        if (existsExecutedOrderInCategory(command.memberId(), asset.category(), requestTime)) {
            throw new ApiException(TradeErrorCode.EXECUTED_TODAY_ORDER_EXISTS_IN_CATEGORY);
        }
    }

    private boolean existsPendingOrderInCategory(Long memberId, AssetsCategory category) {
        List<Order> orders = orderQueryService.findPendingOrders(memberId);

        return orders.stream().anyMatch(pendingOrder -> {
            AssetDto pendingAsset = quoteApiClient.fetchAsset(pendingOrder.getAssetId());
            return category.equals(pendingAsset.category());
        });
    }

    private boolean existsExecutedOrderInCategory(Long memberId, AssetsCategory category, LocalDateTime requestTime) {
        List<Order> orders = orderQueryService.findTodayExecutedOrders(memberId, requestTime);

        return orders.stream().anyMatch(excutedOrder -> {
            AssetDto executedAsset = quoteApiClient.fetchAsset(excutedOrder.getAssetId());
            return category.equals(executedAsset.category());
        });
    }

    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
