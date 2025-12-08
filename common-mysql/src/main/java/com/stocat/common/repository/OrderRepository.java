package com.stocat.common.repository;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByMemberIdAndSideAndCategoryAndStatus(
            Long memberId, TradeSide side, AssetsCategory category, OrderStatus status);

    boolean existsByMemberIdAndSideAndCategoryAndCreatedAtBetween(
            Long memberId, TradeSide side, AssetsCategory category, LocalDateTime start, LocalDateTime end);
}