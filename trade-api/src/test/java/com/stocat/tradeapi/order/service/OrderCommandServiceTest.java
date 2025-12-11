package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrderCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderCommandServiceTest {
    @Mock
    private OrderRepository orderRepository;

    private OrderCommandService orderCommandService;

    @BeforeEach
    void setUp() {
        orderCommandService = new OrderCommandService(orderRepository);
    }

    @Test
    void 매수주문_생성시_주문초기상태는_CREATED이다() {
        BuyOrderCommand command = createBuyOrderCommand();
        given(orderRepository.save(any(Order.class)))
                .will(invocation -> invocation.getArgument(0));

        Order saved = orderCommandService.createBuyOrder(command);

        assertThat(saved).satisfies(o -> {
            assertThat(o.getSide()).isEqualTo(TradeSide.BUY);
            assertThat(o.getStatus()).isEqualTo(OrderStatus.CREATED);
        });
    }

    @Test
    void 주문상태_변경시_Order의_status_필드를_업데이트한다() {
        Order order = createBuyOrder(OrderStatus.PENDING);
        given(orderRepository.save(order)).willReturn(order);

        Order updated = orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderRepository,times(1)).save(order);
    }
}
