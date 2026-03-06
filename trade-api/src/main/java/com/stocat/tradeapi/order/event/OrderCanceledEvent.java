package com.stocat.tradeapi.order.event;

import com.stocat.tradeapi.order.service.dto.OrderDto;

/**
 * 주문 취소 완료 이벤트
 * <p>
 * 주문 취소 트랜잭션이 커밋된 후 발행됩니다.
 * </p>
 */
public record OrderCanceledEvent(
        OrderDto orderDto
) {
}