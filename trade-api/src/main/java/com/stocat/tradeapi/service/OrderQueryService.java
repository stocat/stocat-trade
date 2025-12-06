package com.stocat.tradeapi.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.repository.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public Optional<Order> findById(@NonNull Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findPendingBuyOrdersInCategory(
            @NonNull Long memberId,
            @NonNull AssetsCategory category
    ) {
        return orderRepository.findAllByMemberIdAndSideAndCategoryAndStatus(
                memberId, TradeSide.BUY, category, OrderStatus.PENDING);
    }

    public List<Order> findTodayExecutedBuyOrdersInCategory(
            @NonNull Long memberId,
            @NonNull AssetsCategory category,
            @NonNull LocalDateTime now
    ) {
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(LocalTime.MAX);
        return orderRepository.findAllByMemberIdAndSideAndCategoryAndExecutedAtBetween
                (memberId, TradeSide.BUY, category, todayStart, todayEnd);
    }
}
