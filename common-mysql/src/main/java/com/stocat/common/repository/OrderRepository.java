package com.stocat.common.repository;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberIdAndSideAndCategoryAndStatus(
            Long memberId, TradeSide side, AssetsCategory category, OrderStatus status);

    List<Order> findAllByMemberIdAndSideAndCategoryAndExecutedAtBetween(
            Long memberId, TradeSide side, AssetsCategory category, LocalDateTime start, LocalDateTime end);


}
