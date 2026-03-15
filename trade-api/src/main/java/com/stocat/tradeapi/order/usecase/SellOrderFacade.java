package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.OrderQueryService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import com.stocat.tradeapi.position.service.PositionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매도 주문 처리 Facade
 * <p>
 * 매도 주문 생성 및 취소와 관련된 도메인 로직(주문, 포지션)을 트랜잭션 단위로 묶어 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SellOrderFacade {
    private final OrderCommandService orderCommandService;
    private final PositionQueryService positionQueryService;
    private final OrderQueryService orderQueryService;

    @Transactional
    public OrderDto processSellOrder(SellOrderCommand command, AssetDto asset) {
        reservePosition(command, asset);

        Order order = orderCommandService.createSellOrder(command, asset);
        return OrderDto.from(order);
    }

    /**
     * 매도 주문 취소 (사용자 요청)
     * <p>
     * 사용자가 직접 주문을 취소할 때 호출됩니다. 호출한 쪽의 트랜잭션에 참여합니다.
     * </p>
     *
     * @param order 취소할 주문
     */
    @Transactional
    public OrderDto cancelSellOrder(Order order) {
        return internalCancelSellOrder(order);
    }

    /**
     * 매도 주문 취소 및 포지션 예약 해제 (보상 트랜잭션)
     * <p>
     * 외부 매칭 엔진 전송 실패 등으로 인해 주문을 취소해야 할 때 호출됩니다. 항상 새로운 트랜잭션(REQUIRES_NEW)으로 실행되어, 호출한 쪽의 트랜잭션 상태와 무관하게 커밋됩니다.
     * </p>
     *
     * @param orderId 취소할 주문 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void compensateSellOrder(Long orderId) {
        Order order = orderQueryService.findByIdForUpdate(orderId);
        internalCancelSellOrder(order);
    }

    private OrderDto internalCancelSellOrder(Order order) {
        validateCancelSellOrder(order.getStatus(), order.getSide());

        // 상태 변경 (취소)
        orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);

        // 포지션 예약 해제
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(order.getAssetId(), order.getUserId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));
        position.release(order.getQuantity());

        return OrderDto.from(order);
    }

    private void reservePosition(SellOrderCommand command, AssetDto asset) {
        PositionEntity position = positionQueryService
                .getUserPositionForUpdate(asset.id(), command.userId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.POSITION_NOT_FOUND_FOR_SELL));

        position.reserve(command.quantity());
    }

    private void validateCancelSellOrder(OrderStatus status, TradeSide side) {
        if (status != OrderStatus.PENDING) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_STATUS);
        }

        if (side != TradeSide.SELL) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_SIDE);
        }
    }
}
