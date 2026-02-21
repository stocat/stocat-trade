package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderQueryService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyOrderUsecase {
    private final QuoteApiClient quoteApiClient;
    private final MatchApiClient matchApiClient;
    private final BuyOrderFacade buyOrderFacade;
    private final OrderQueryService orderQueryService;

    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());
        validateAsset(asset);
        validateBuyOrder(command, asset);

        OrderDto orderDto = buyOrderFacade.processBuyOrder(command, asset);
        matchApiClient.submitBuyOrder(BuyOrderSubmissionRequest.from(orderDto));
        return orderDto;
    }

    private void validateAsset(AssetDto asset) {
        if (asset == null) {
            throw new ApiException(TradeErrorCode.ASSET_NOT_FOUND);
        }
        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
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
}
