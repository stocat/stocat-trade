package com.stocat.tradeapi.service;

import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.service.dto.command.OrderCancelCommand;

public interface OrderServicePort {
    OrderDto placeBuyOrder(BuyOrderCommand command);
    OrderDto cancelOrder(OrderCancelCommand command);
}
