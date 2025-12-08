package com.stocat.tradeapi.service;

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

    public Order findById(@NonNull Long orderId) {
        return orderRepository.findById(orderId)
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

    /**
     * 특정 시간 이전에 생성된 주문들을 조회
     * 스케줄러에서 재시도 대상 주문을 찾을 때 사용
     */
    public List<Order> findCreatedOrdersOlderThan(@NonNull LocalDateTime before) {
        return orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.CREATED, before);
    }
}
