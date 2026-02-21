package com.stocat.tradeapi.fill.usecase;

import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.fill.service.TradeFillService;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.position.service.PositionService;
import com.stocat.tradeapi.position.service.dto.command.PositionUpsertCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BuyOrderExecutionUsecase {
    private final TradeFillService tradeFillService;
    private final OrderService orderService;
    private final PositionService positionService;
    private final CashService cashService;

    @Transactional
    public void fillBuyOrder(FillBuyOrderCommand command) {
        OrderDto order = orderService.fillBuyOrder(command);
        tradeFillService.fillBuyOrder(command, order);

        // 포지션 수량 업데이트 (구매한만큼 포지션 수량 수정)
        PositionUpsertCommand upsertCommand = new PositionUpsertCommand(
                order.userId(),
                order.assetId(),
                command.quantity(),
                command.price()
        );
        positionService.updateUserPosition(upsertCommand);

        // 캐시 홀딩 테이블 삭제 및 사용자 보유 금액 차감
        cashService.consumeHoldingAndWithdraw(order.cashHoldingId());
    }
}
