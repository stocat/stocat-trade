package com.stocat.tradeapi.order.service;

import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class OrderServiceAdapter implements OrderServicePort {
    private final OrderService orderService;
    private final QuoteApiClient quoteApiClient;

    @Override
    public OrderDto placeBuyOrder(BuyOrderCommand command) {
        AssetDto asset = quoteApiClient.fetchAsset(command.asset().symbol());

        BuyOrderCommand completedCommand = BuyOrderCommand.builder()
                .memberId(command.memberId())
                .asset(asset)
                .quantity(command.quantity())
                .price(command.price())
                .build();

        return orderService.placeBuyOrder(completedCommand);
    }

    @Override
    public OrderDto cancelOrder(OrderCancelCommand command) {
        return orderService.cancelOrder(command);
    }
}
