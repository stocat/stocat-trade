package com.stocat.tradeapi.service;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {
    private final OrderRepository orderRepository;

    public Order createBuyOrder(BuyOrderCommand command) {
        Order order = Order.builder()
                .memberId(command.memberId())
                .assetId(command.asset().id())
                .currency(command.asset().currency())
                .category(command.asset().category())
                .side(TradeSide.BUY)
                .status(OrderStatus.SUBMITTED)
                .quantity(command.quantity())
                .price(command.price())
                .build();

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(
            @NonNull Long orderId,
            @NonNull OrderStatus status
    ) {
        orderRepository.findById(orderId);
    }
}
