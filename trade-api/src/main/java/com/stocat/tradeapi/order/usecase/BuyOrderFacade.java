package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.order.Order;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyOrderFacade {
    private final CashService cashService;
    private final OrderCommandService orderCommandService;

    @Transactional
    public OrderDto processBuyOrder(BuyOrderCommand command, AssetDto asset) {
        CashHoldingEntity holding = holdCash(asset, command);
        Order order = orderCommandService.createBuyOrder(command, asset, holding.getId());
        return OrderDto.from(order);
    }

    private CashHoldingEntity holdCash(AssetDto asset, BuyOrderCommand command) {
        if (asset.currency() == null || command.price() == null) {
            throw new ApiException(TradeErrorCode.INTERNAL_ERROR);
        }
        CreateCashHoldingCommand holdingCommand = new CreateCashHoldingCommand(
                command.userId(),
                asset.currency(),
                command.price().multiply(command.quantity())
        );
        return cashService.createCashHolding(holdingCommand);
    }
}
