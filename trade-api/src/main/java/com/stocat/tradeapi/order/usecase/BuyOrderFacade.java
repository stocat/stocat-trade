package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.OrderQueryService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyOrderFacade {
    private final CashService cashService;
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @Transactional
    public OrderDto processBuyOrder(BuyOrderCommand command, AssetDto asset) {
        Long holdingId = holdCash(asset, command);
        Order order = orderCommandService.createBuyOrder(command, asset, holdingId);
        return OrderDto.from(order);
    }

    /**
     * 매수 주문 취소 (사용자 요청)
     * <p>
     * 사용자가 직접 매수 주문을 취소할 때 호출됩니다.
     * 주문 상태를 취소(CANCELED)로 변경하고, 해당 주문을 위해 홀딩된 현금을 해제합니다.
     * </p>
     *
     * @param orderId 취소할 주문 ID
     * @param userId  요청한 사용자 ID
     * @return 취소된 주문 정보
     */
    @Transactional
    public OrderDto cancelBuyOrder(Long orderId, Long userId) {
        Order order = orderQueryService.findById(orderId);
        validateCancelBuyOrder(userId, order);

        // 상태 변경 (취소)
        orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);

        // 매수 주문 취소 시 홀딩된 현금 해제
        releaseCashHolding(order.getCashHoldingId());

        return OrderDto.from(order);
    }

    private Long holdCash(AssetDto asset, BuyOrderCommand command) {
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

    private void releaseCashHolding(Long cashHoldingId) {
        if (cashHoldingId == null) {
            throw new ApiException(TradeErrorCode.INTERNAL_ERROR);
        }
        cashService.releaseCashHolding(cashHoldingId);
    }

    private void validateCancelBuyOrder(Long userId, Order order) {
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new ApiException(TradeErrorCode.ORDER_NOT_FOUND);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_STATUS);
        }

        if (order.getSide() != TradeSide.BUY) {
            throw new ApiException(TradeErrorCode.INVALID_ORDER_SIDE);
        }
    }
}
