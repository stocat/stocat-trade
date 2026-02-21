package com.stocat.tradeapi.fill.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.fill.service.TradeFillService;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.position.service.PositionService;
import com.stocat.tradeapi.position.service.dto.command.PositionUpsertCommand;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyOrderExecutionUsecaseTest {

    @Mock
    private TradeFillService tradeFillService;
    @Mock
    private OrderService orderService;
    @Mock
    private PositionService positionService;
    @Mock
    private CashService cashService;

    @InjectMocks
    private BuyOrderExecutionUsecase usecase;

    private FillBuyOrderCommand command;
    private OrderDto order;

    @BeforeEach
    void setUp() {
        command = new FillBuyOrderCommand(
                1L,
                10L,
                TradeSide.BUY,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(100),
                null,
                BigDecimal.ZERO,
                null,
                LocalDateTime.now(),
                null,
                BigDecimal.ONE,
                "USD/KRW"
        );

        order = new OrderDto(
                1L,
                7L,
                10L,
                55L,
                TradeSide.BUY,
                OrderType.MARKET,
                OrderStatus.FILLED,
                command.quantity(),
                command.price(),
                OrderTif.GTC,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void 매수체결시_주문상태_체결_포지션_업데이트_및_현금소진_성공() {
        when(orderService.fillBuyOrder(command)).thenReturn(order);

        usecase.fillBuyOrder(command);

        verify(orderService).fillBuyOrder(command);
        verify(tradeFillService).fillBuyOrder(command, order);
        ArgumentCaptor<PositionUpsertCommand> captor = ArgumentCaptor.forClass(PositionUpsertCommand.class);
        verify(positionService).updateUserPosition(captor.capture());
        PositionUpsertCommand upsert = captor.getValue();
        assertThat(upsert.userId()).isEqualTo(order.userId());
        assertThat(upsert.assetId()).isEqualTo(order.assetId());
        assertThat(upsert.quantity()).isEqualByComparingTo(command.quantity());
        assertThat(upsert.avgEntryPrice()).isEqualByComparingTo(command.price());

        verify(cashService).consumeHoldingAndWithdraw(order.cashHoldingId());
    }

    @Test
    void 주문_상태_업데이트_실패시_DB에_변경사항이_없다() {
        RuntimeException failure = new RuntimeException("order failure");
        when(orderService.fillBuyOrder(command)).thenThrow(failure);

        assertThatThrownBy(() -> usecase.fillBuyOrder(command)).isSameAs(failure);

        verify(orderService).fillBuyOrder(command);
        verifyNoInteractions(tradeFillService, positionService, cashService);
    }

    @Test
    void 체결_저장_실패시_주문_변경도_롤백된다() {
        when(orderService.fillBuyOrder(command)).thenReturn(order);
        RuntimeException failure = new RuntimeException("trade fill failure");
        doThrow(failure).when(tradeFillService).fillBuyOrder(command, order);

        assertThatThrownBy(() -> usecase.fillBuyOrder(command)).isSameAs(failure);

        verify(orderService).fillBuyOrder(command);
        verify(tradeFillService).fillBuyOrder(command, order);
        verifyNoInteractions(positionService, cashService);
    }

    @Test
    void 포지션_업데이트_실패시_주문과_체결_롤백된다() {
        when(orderService.fillBuyOrder(command)).thenReturn(order);
        RuntimeException failure = new RuntimeException("position failure");
        doThrow(failure).when(positionService).updateUserPosition(any(PositionUpsertCommand.class));

        assertThatThrownBy(() -> usecase.fillBuyOrder(command)).isSameAs(failure);

        verify(orderService).fillBuyOrder(command);
        verify(tradeFillService).fillBuyOrder(command, order);
        verify(positionService).updateUserPosition(any(PositionUpsertCommand.class));
        verifyNoInteractions(cashService);
    }

    @Test
    void 현금_소진_실패시_전체_롤백된다() {
        when(orderService.fillBuyOrder(command)).thenReturn(order);
        RuntimeException failure = new RuntimeException("cash failure");
        doThrow(failure).when(cashService).consumeHoldingAndWithdraw(order.cashHoldingId());

        assertThatThrownBy(() -> usecase.fillBuyOrder(command)).isSameAs(failure);

        verify(orderService).fillBuyOrder(command);
        verify(tradeFillService).fillBuyOrder(command, order);
        verify(positionService).updateUserPosition(any(PositionUpsertCommand.class));
        verify(cashService).consumeHoldingAndWithdraw(order.cashHoldingId());
    }
}
