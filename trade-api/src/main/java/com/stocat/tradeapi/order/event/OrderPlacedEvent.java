package com.stocat.tradeapi.order.event;

import com.stocat.tradeapi.order.service.dto.OrderDto;

/**
 * 주문 생성 완료 이벤트
 * <p>
 * 주문이 DB에 성공적으로 저장된 후 발행되는 이벤트입니다. 이후 리스너에서 외부 매칭 엔진으로 주문을 전송하는 등의 후속 작업을 처리합니다.
 * </p>
 *
 * @param orderDto 생성된 주문 정보
 */
public record OrderPlacedEvent(
        OrderDto orderDto
) {
}
