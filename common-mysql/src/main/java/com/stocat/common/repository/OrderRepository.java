package com.stocat.common.repository;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberIdAndStatus(Long memberId, OrderStatus status);
    List<Order> findAllByMemberIdAndExecutedAtBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
