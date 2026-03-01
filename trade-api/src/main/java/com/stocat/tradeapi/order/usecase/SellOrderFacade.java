package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import com.stocat.tradeapi.position.service.PositionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellOrderFacade {
    private final OrderCommandService orderCommandService;
    private final PositionQueryService positionQueryService;

    @Transactional
    public OrderDto processSellOrder(SellOrderCommand command, AssetDto asset) {
        validatePosition(command, asset);

        Order order = orderCommandService.createSellOrder(command, asset);
        return OrderDto.from(order);
    }

    private void validatePosition(SellOrderCommand command, AssetDto asset) {
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(asset.id(), command.userId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));

        if (position.getQuantity().compareTo(command.quantity()) < 0) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_POSITION_QUANTITY);
        }
    }
}
