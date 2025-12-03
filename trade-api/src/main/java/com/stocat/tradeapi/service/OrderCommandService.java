package com.stocat.tradeapi.service;

import com.stocat.tradeapi.domain.Order;
import com.stocat.tradeapi.domain.OrderSide;
import com.stocat.tradeapi.domain.OrderStatus;
import com.stocat.tradeapi.infrastructure.AssetDto;
import com.stocat.tradeapi.repository.OrderRepository;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {
    private final OrderRepository orderRepository;

    public Order createBuyOrder(BuyOrderCommand command, AssetDto asset) {
        Order order = Order.builder()
                .memberId(command.memberId())
                .assetId(asset.id())
                .side(OrderSide.BUY)
                .currency(asset.currency())
                .status(OrderStatus.PENDING)
                .quantity(command.quantity())
                .price(command.price())
                .build();

        return orderRepository.save(order);
    }
}
