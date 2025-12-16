package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public Order findById(@NonNull Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Order findByIdForUpdate(@NonNull Long orderId) {
        return orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.ORDER_NOT_FOUND));
    }

    public boolean existsPendingBuyOrdersInCategory(
            @NonNull Long memberId,
            @NonNull AssetsCategory category
    ) {
        return orderRepository.existsByMemberIdAndSideAndCategoryAndStatus(
                memberId, TradeSide.BUY, category, OrderStatus.PENDING);
    }

    public boolean existsTodayExecutedBuyOrdersInCategory(
            @NonNull Long memberId,
            @NonNull AssetsCategory category,
            @NonNull LocalDateTime now
    ) {
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(LocalTime.MAX);
        return orderRepository.existsByMemberIdAndSideAndCategoryAndCreatedAtBetween
                (memberId, TradeSide.BUY, category, todayStart, todayEnd);
    }
}
