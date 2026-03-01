package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    @Transactional
    public OrderDto fillBuyOrder(FillBuyOrderCommand command) {
        Order order = orderQueryService.findByIdForUpdate(command.orderId());
        // TODO: 부분 체결이 있다면, 부분체결 상황을 알기 위한 업데이트 추가
        // TODO: 부분 체결을 위한 추가 테이블 혹은 필드가 있다면 추가
        orderCommandService.updateOrderStatus(order, OrderStatus.FILLED);

        return OrderDto.from(order);
    }


    private void validateSellOrder(OrderDto orderDto) {
        // TODO: 구현
        throw new NotImplementedException();
    }
}
