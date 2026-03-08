package com.stocat.tradeapi.order.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyOrderFacadeTest {

    @InjectMocks
    private BuyOrderFacade buyOrderFacade;

    @Mock
    private CashService cashService;

    @Mock
    private OrderCommandService orderCommandService;

    @Test
    @DisplayName("정상적인 매수 주문 취소 요청 시 상태가 변경되고 현금 홀딩이 해제된다")
    void cancelBuyOrder_Success() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        Long cashHoldingId = 500L;

        Order order = mock(Order.class);
        given(order.getId()).willReturn(orderId);
        given(order.getUserId()).willReturn(userId);
        given(order.getStatus()).willReturn(OrderStatus.PENDING);
        given(order.getSide()).willReturn(TradeSide.BUY);
        given(order.getCashHoldingId()).willReturn(cashHoldingId);

        // when
        OrderDto result = buyOrderFacade.cancelBuyOrder(order);

        // then
        assertThat(result.id()).isEqualTo(orderId);
        verify(orderCommandService).updateOrderStatus(order, OrderStatus.CANCELED);
        verify(cashService).releaseCashHolding(cashHoldingId);
    }

    @Test
    @DisplayName("주문 상태가 PENDING이 아니면 예외가 발생한다")
    void cancelBuyOrder_InvalidStatus() {
        // given
        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(OrderStatus.FILLED); // 이미 체결됨

        // when & then
        assertThatThrownBy(() -> buyOrderFacade.cancelBuyOrder(order))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode").isEqualTo(TradeErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("주문 타입이 BUY가 아니면 예외가 발생한다")
    void cancelBuyOrder_InvalidSide() {
        // given
        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(OrderStatus.PENDING);
        given(order.getSide()).willReturn(TradeSide.SELL); // 매도 주문

        // when & then
        assertThatThrownBy(() -> buyOrderFacade.cancelBuyOrder(order))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode").isEqualTo(TradeErrorCode.INVALID_ORDER_SIDE);
    }
}
