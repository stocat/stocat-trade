package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import lombok.NonNull;
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
                .side(TradeSide.BUY)
                .status(OrderStatus.PENDING)
                .quantity(command.quantity())
                .price(command.price())
                .build();

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(
            @NonNull Order order,
            @NonNull OrderStatus targetStatus
    ) {
        order.updateStatus(targetStatus);
        return orderRepository.save(order);
    }
}
