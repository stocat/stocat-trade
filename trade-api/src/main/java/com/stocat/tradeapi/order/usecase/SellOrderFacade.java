package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Transactional
    public OrderDto processSellOrder(SellOrderCommand command, AssetDto asset) {
        reservePosition(command, asset);

        Order order = orderCommandService.createSellOrder(command, asset);
        return OrderDto.from(order);
    }

    @Transactional
    public void cancelSellOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.ORDER_NOT_FOUND));

        if (order.getSide() != TradeSide.SELL) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_SIDE);
        }

        // 상태 변경 (취소)
        orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);

        // 포지션 예약 해제
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(order.getAssetId(), order.getUserId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));

        position.release(order.getQuantity());
    }

    private void reservePosition(SellOrderCommand command, AssetDto asset) {
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(asset.id(), command.userId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));

        position.reserve(command.quantity());
    }
}
