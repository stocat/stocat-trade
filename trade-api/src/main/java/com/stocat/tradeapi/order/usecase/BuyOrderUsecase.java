package com.stocat.tradeapi.order.usecase;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BuyOrderUsecase {
    private final OrderService orderService;
    private final QuoteApiClient quoteApiClient;
    private final CashService cashService;

    @Transactional
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.assetSymbol());
        validateAsset(asset);

        OrderDto order = orderService.placeBuyOrder(command, asset);
        holdCash(order, asset, command);

        return order;
    }

    private void validateAsset(AssetDto asset) {
//        if (asset == null) {
//            throw new ApiException(TradeErrorCode.ASSET_NOT_FOUND);
//        }
        if (!asset.isDaily()) {
            throw new ApiException(TradeErrorCode.NOT_DAILY_PICK_ASSET);
        }
    }

    private void holdCash(OrderDto order, AssetDto asset, BuyOrderCommand command) {
        if (asset.currency() == null || command.price() == null) {
            throw new ApiException(TradeErrorCode.INTERNAL_ERROR);
        }
        CreateCashHoldingCommand holdingCommand = new CreateCashHoldingCommand(
                order.userId(),
                asset.currency(),
                order.id(),
                command.price().multiply(command.quantity())
        );
        cashService.createCashHolding(holdingCommand);
    }
}
