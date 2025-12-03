package com.stocat.tradeapi.service;

import com.stocat.tradeapi.domain.Order;
import com.stocat.tradeapi.domain.OrderStatus;
import com.stocat.tradeapi.repository.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public List<Order> findPendingOrders(
            @NonNull Long memberId

    ) {
        return orderRepository.findAllByMemberIdAndStatus(memberId, OrderStatus.PENDING);
    }

    public List<Order> findTodayExecutedOrders(
            @NonNull Long memberId,
            @NonNull LocalDateTime now
    ) {
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(LocalTime.MAX);
        return orderRepository.findAllByMemberIdAndExecutedAtBetween(memberId, todayStart, todayEnd);
    }
}
