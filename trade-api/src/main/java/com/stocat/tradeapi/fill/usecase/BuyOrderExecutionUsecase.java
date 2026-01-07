package com.stocat.tradeapi.fill.usecase;

import com.stocat.tradeapi.fill.service.TradeFillService;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyOrderExecutionUsecase {
    private final TradeFillService tradeFillService;
    private final OrderService orderService;

    public void fillBuyOrder(FillBuyOrderCommand command) {
        OrderDto order = orderService.fillBuyOrder(command);
        tradeFillService.fillBuyOrder(command, order);

        // TODO: 매수 성공에 따른 포지션 추가 필요 (ADD)
    }
}
