package com.stocat.tradeapi.fill.service;


import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeFillService {
    private final TradeFillCommandService commandService;

    public void fillBuyOrder(FillBuyOrderCommand command, OrderDto order) {
        commandService.createTradeFill(command, order);
    }
}
