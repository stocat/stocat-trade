package com.stocat.tradeapi.order.service;

import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
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
import java.util.List;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrderCommand;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createCryptoAssetDto;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createUsdAssetDto;
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
    private QuoteApiClient quoteApiClient;
    @Mock
    private MatchApiClient matchApiClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderQueryService, orderCommandService, quoteApiClient, matchApiClient);
    }

    @Test
    void 매수주문시_주문생성은_OrderCommandService에_위임한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        Order order = createBuyOrder(command);
        given(orderCommandService.createBuyOrder(command, asset)).willReturn(order);

        OrderDto orderDto = orderService.placeBuyOrder(command, asset);

        verify(orderCommandService, times(1)).createBuyOrder(command, asset);
        assertThat(orderDto.id()).isEqualTo(order.getId());
    }

    @Test
    void 매수주문시_주문을_체결엔진에_제출한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        Order order = createBuyOrder(command);
        given(orderCommandService.createBuyOrder(command, asset)).willReturn(order);

        orderService.placeBuyOrder(command, asset);

        verify(matchApiClient, times(1)).submitBuyOrder(BuyOrderSubmissionRequest.from(order));
    }


    @Test
    void 코인매수주문시_수량이_소수점4자리를_초과하면_예외가_발생한다() {
        BuyOrderCommand command = BuyOrderCommand.builder().userId(1L).orderType(OrderType.LIMIT).assetSymbol("BTC/KRW").price(BigDecimal.valueOf(200)).requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .quantity(BigDecimal.valueOf(0.12345))
                .build();
        AssetDto asset = createCryptoAssetDto();

        assertThatThrownBy(() -> orderService.placeBuyOrder(command, asset))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INVALID_ORDER_QUANTITY);
    }

    @Test
    void 주식매수주문시_수량이_정수가아니면_예외가_발생한다() {
        BuyOrderCommand command = BuyOrderCommand.builder().userId(1L).orderType(OrderType.LIMIT).assetSymbol("NVDA").price(BigDecimal.valueOf(200)).requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .quantity(BigDecimal.valueOf(0.1))
                .build();
        AssetDto asset = createUsdAssetDto();


        assertThatThrownBy(() -> orderService.placeBuyOrder(command, asset))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INVALID_ORDER_QUANTITY);
    }

    @Test
    void 매수주문시_동일_카테고리에_취소되지_않은_주문이_있으면_예외가_발생한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();

        Order previousOrder = createBuyOrder(OrderStatus.PENDING);
        given(orderQueryService.findUserBuyOrdersToday(command.userId(), command.requestTime()))
                .willReturn(List.of(previousOrder));
        given(quoteApiClient.fetchAssetById(previousOrder.getAssetId())).willReturn(asset);

        assertThatThrownBy(() -> orderService.placeBuyOrder(command, asset))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.BUY_ORDER_LIMIT_PER_CATEGORY);
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
