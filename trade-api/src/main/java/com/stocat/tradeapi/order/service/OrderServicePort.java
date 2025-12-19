package com.stocat.tradeapi.order.service;

import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;

public interface OrderServicePort {
    OrderDto placeBuyOrder(BuyOrderCommand command);
    OrderDto cancelOrder(OrderCancelCommand command);
}
