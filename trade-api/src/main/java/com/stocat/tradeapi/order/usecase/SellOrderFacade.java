package com.stocat.tradeapi.order.usecase;

import com.stocat.common.domain.order.Order;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellOrderFacade {
    private final OrderCommandService orderCommandService;

    @Transactional
    public OrderDto processSellOrder(SellOrderCommand command, AssetDto asset) {
        Order order = orderCommandService.createSellOrder(command, asset);
        return OrderDto.from(order);
    }
}
