package com.stocat.tradeapi.service;

import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;

public interface OrderServicePort {
    OrderDto placeBuyOrder(BuyOrderCommand command);
}
