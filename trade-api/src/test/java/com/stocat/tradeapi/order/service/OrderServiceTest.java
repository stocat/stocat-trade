package com.stocat.tradeapi.order.service;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.order.OrderFixtureUtils;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private MatchApiClient matchApiClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderQueryService, orderCommandService, matchApiClient);
    }

    @Test
    void 주문취소시_주문업데이트는_OrderCommandService에_위임한다() {
        OrderCancelCommand command = new OrderCancelCommand(1000L, 1L);
        Order order = createBuyOrder(OrderStatus.PENDING);
        given(orderQueryService.findByIdForUpdate(command.orderId())).willReturn(order);
        given(orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED))
                .willReturn(OrderFixtureUtils.createBuyOrder(OrderStatus.CANCELED));

        orderService.cancelOrder(command);

        verify(orderCommandService, times(1))
                .updateOrderStatus(order, OrderStatus.CANCELED);
    }

    @Test
    void 주문취소시_주문소유자와_요청자가_다르면_예외가_발생한다() {
        OrderCancelCommand command = new OrderCancelCommand(1000L, 2L);
        given(orderQueryService.findByIdForUpdate(command.orderId())).willReturn(
                OrderFixtureUtils.createBuyOrder(OrderStatus.PENDING));

        assertThatThrownBy(() -> orderService.cancelOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.ORDER_PERMISSION_DENIED);
    }
}
