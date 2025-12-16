package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.OrderFixtureUtils;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createAssetDto;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrderCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void 매수주문시_주문생성은_OrderCommandService에_위임한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        Order order = createBuyOrder(command);
        given(orderCommandService.createBuyOrder(command)).willReturn(order);

        OrderDto orderDto = orderService.placeBuyOrder(command);

        verify(orderCommandService, times(1)).createBuyOrder(command);
        assertThat(orderDto.id()).isEqualTo(order.getId());
    }


    @Test
    void 코인매수주문시_수량이_소수점4자리를_초과하면_예외가_발생한다() {
        AssetDto asset = AssetDto.builder().id(1).symbol("BTC/KRW").currency(Currency.KRW).isActive(true).isDaily(true).koName("비트코인").usName("BTC")
                .category(AssetsCategory.CRYPTO)
                .build();

        BuyOrderCommand command = BuyOrderCommand.builder().memberId(1L).orderType(OrderType.LIMIT).asset(asset).price(BigDecimal.valueOf(200)).requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .quantity(BigDecimal.valueOf(0.12345))
                .build();

        assertThatThrownBy(() -> orderService.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INVALID_ORDER_QUANTITY);
    }

    @Test
    void 주식매수주문시_수량이_정수가아니면_예외가_발생한다() {
        BuyOrderCommand command = BuyOrderCommand.builder().memberId(1L).orderType(OrderType.LIMIT).asset(createAssetDto()).price(BigDecimal.valueOf(200)).requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .quantity(BigDecimal.valueOf(0.1))
                .build();

        assertThatThrownBy(() -> orderService.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INVALID_ORDER_QUANTITY);
    }

    @Test
    void 매수주문시_종목이_데일리픽이_아니면_예외가_발생한다() {
        AssetDto asset = AssetDto.builder().id(1).symbol("NVDA").category(AssetsCategory.USD).currency(Currency.USD).isActive(true).koName("엔비디아").usName("NVIDIA")
                .isDaily(false).build();
        BuyOrderCommand command = createBuyOrderCommand(asset);

        assertThatThrownBy(() -> orderService.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.NOT_DAILY_PICK_ASSET);
    }

    @Test
    void 매수주문시_동일_카테고리에_체결대기중인_종목이_있으면_예외가_발생한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        given(orderQueryService.existsPendingBuyOrdersInCategory(command.memberId(), command.asset().category()))
                .willReturn(true);

        assertThatThrownBy(() -> orderService.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.PENDING_ORDER_EXISTS_IN_CATEGORY);
    }

    @Test
    void 매수주문시_동일_카테고리에_금일_매수체결된_거래가_있으면_예외가_발생한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        given(orderQueryService.existsTodayExecutedBuyOrdersInCategory(command.memberId(), command.asset().category(), command.requestTime()))
                .willReturn(true);

        assertThatThrownBy(() -> orderService.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.EXECUTED_TODAY_ORDER_EXISTS_IN_CATEGORY);
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
    void 주문취소시_체결엔진을_통해_검증_한다() {
        OrderCancelCommand command = new OrderCancelCommand(1000L, 1L);
        Order order = createBuyOrder(OrderStatus.PENDING);
        given(orderQueryService.findByIdForUpdate(command.orderId())).willReturn(order);
        given(orderCommandService.updateOrderStatus(order, OrderStatus.CANCELED))
                .willReturn(OrderFixtureUtils.createBuyOrder(OrderStatus.CANCELED));

        orderService.cancelOrder(command);

        verify(matchApiClient, times(1))
                .cancelOrder(command.orderId());
    }

    @Test
    void 주문취소시_주문소유자와_요청자가_다르면_예외가_발생한다() {
        OrderCancelCommand command = new OrderCancelCommand(1000L, 2L);
        given(orderQueryService.findByIdForUpdate(command.orderId())).willReturn(OrderFixtureUtils.createBuyOrder(OrderStatus.PENDING));

        assertThatThrownBy(() -> orderService.cancelOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.ORDER_PERMISSION_DENIED);
    }
}
