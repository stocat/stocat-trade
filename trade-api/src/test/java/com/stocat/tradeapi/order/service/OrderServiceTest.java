package com.stocat.tradeapi.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiFacade;
import com.stocat.tradeapi.order.event.OrderCanceledEvent;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import com.stocat.tradeapi.order.usecase.BuyOrderFacade;
import com.stocat.tradeapi.order.usecase.CancelOrderUsecase;
import com.stocat.tradeapi.order.usecase.SellOrderFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private CancelOrderUsecase cancelOrderUsecase;

    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private SellOrderFacade sellOrderFacade;
    @Mock
    private BuyOrderFacade buyOrderFacade;
    @Mock
    private MatchApiFacade matchApiFacade;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("주문 취소 시 SellOrderFacade에 위임하고 이벤트를 발행한다")
    void cancelOrder_DelegatesToSellOrderFacadeAndPublishesEvent() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        OrderCancelCommand command = new OrderCancelCommand(orderId, userId);

        Order order = mock(Order.class);
        given(order.getUserId()).willReturn(userId);
        given(order.getStatus()).willReturn(OrderStatus.PENDING);
        given(order.getSide()).willReturn(TradeSide.SELL);

        given(orderQueryService.findById(orderId)).willReturn(order);

        OrderDto canceledOrderDto = mock(OrderDto.class);
        given(canceledOrderDto.status()).willReturn(OrderStatus.CANCELED);
        given(sellOrderFacade.cancelSellOrder(orderId, userId)).willReturn(canceledOrderDto);

        // when
        OrderDto result = cancelOrderUsecase.cancelOrder(command);

        // then
        assertThat(result.status()).isEqualTo(OrderStatus.CANCELED);
        verify(sellOrderFacade).cancelSellOrder(orderId, userId);
        verify(eventPublisher).publishEvent(any(OrderCanceledEvent.class));
    }

    @Test
    @DisplayName("주문 취소 시 주문 소유자와 요청자가 다르면 예외가 발생한다")
    void cancelOrder_ThrowsException_WhenUserMismatch() {
        // given
        Long orderId = 1L;
        Long ownerId = 100L;
        Long requesterId = 200L;
        OrderCancelCommand command = new OrderCancelCommand(orderId, requesterId);

        Order order = mock(Order.class);
        given(order.getUserId()).willReturn(ownerId);

        given(orderQueryService.findById(orderId)).willReturn(order);

        // when & then
        assertThatThrownBy(() -> cancelOrderUsecase.cancelOrder(command))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(TradeErrorCode.ORDER_NOT_FOUND);
    }
}
