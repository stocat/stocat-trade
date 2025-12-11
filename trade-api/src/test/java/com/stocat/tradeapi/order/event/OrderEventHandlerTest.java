package com.stocat.tradeapi.order.event;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyRequest;
import com.stocat.tradeapi.infrastructure.dto.MatchBuyResult;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.OrderQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderEventHandlerTest {
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private MatchApiClient matchApiClient;

    private OrderEventHandler orderEventHandler;

    @BeforeEach
    void setUp() {
        orderEventHandler = new OrderEventHandler(orderQueryService, orderCommandService, matchApiClient);
    }

    @Test
    void 매수주문생성_이벤트_발생시_체결엔진에_주문을_전달한다() {
        Order order = createBuyOrder(OrderStatus.CREATED);
        BuyOrderCreatedEvent event = BuyOrderCreatedEvent.from(order);

        given(orderQueryService.findById(order.getId())).willReturn(order);
        given(matchApiClient.buy(any(MatchBuyRequest.class))).willReturn(new MatchBuyResult("success"));

        orderEventHandler.handleBuyOrderCreatedEvent(event);

        verify(matchApiClient, times(1)).buy(any(MatchBuyRequest.class));
    }

    @Test
    void 체결엔진에_매수주문전달시_성공하면_주문을_PENDING_상태로_업데이트한다() {
        Order order = createBuyOrder(OrderStatus.CREATED);
        BuyOrderCreatedEvent event = BuyOrderCreatedEvent.from(order);

        given(orderQueryService.findById(order.getId())).willReturn(order);
        given(matchApiClient.buy(any(MatchBuyRequest.class))).willReturn(new MatchBuyResult("success"));

        orderEventHandler.handleBuyOrderCreatedEvent(event);

        verify(orderCommandService, times(1)).updateOrderStatus(order, OrderStatus.PENDING);
    }

    @Test
    void 체결엔진에_매수주문전달시_거절되면_주문을_REJECTED_상태로_업데이트한다() {
        Order order = createBuyOrder(OrderStatus.CREATED);
        BuyOrderCreatedEvent event = BuyOrderCreatedEvent.from(order);

        given(orderQueryService.findById(order.getId())).willReturn(order);
        given(matchApiClient.buy(any(MatchBuyRequest.class))).willReturn(new MatchBuyResult("rejected"));

        orderEventHandler.handleBuyOrderCreatedEvent(event);

        verify(orderCommandService, times(1)).updateOrderStatus(order, OrderStatus.REJECTED);
    }
}
