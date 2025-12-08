package com.stocat.tradeapi.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.event.BuyOrderCreatedEvent;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private MatchApiClient matchApiClient;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderQueryService, orderCommandService, matchApiClient, eventPublisher);
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
    void 매수주문시_주문생성_이벤트를_발행한다() {
        BuyOrderCommand command = createBuyOrderCommand();
        Order order = createBuyOrder(command);
        BuyOrderCreatedEvent event = BuyOrderCreatedEvent.from(order);
        given(orderCommandService.createBuyOrder(command)).willReturn(order);

        orderService.placeBuyOrder(command);

        verify(eventPublisher, times(1)).publishEvent(event);
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

    private BuyOrderCommand createBuyOrderCommand() {
        return createBuyOrderCommand(createAssetDto());
    }

    private BuyOrderCommand createBuyOrderCommand(AssetDto asset) {
        return BuyOrderCommand.builder()
                .memberId(1L)
                .orderType(OrderType.LIMIT)
                .asset(asset)
                .price(BigDecimal.valueOf(200))
                .quantity(BigDecimal.valueOf(100))
                .requestTime(LocalDateTime.of(2025, 12, 1, 0, 0, 0))
                .build();
    }

    private AssetDto createAssetDto() {
        return AssetDto.builder()
                .id(1)
                .symbol("NVDA")
                .category(AssetsCategory.USD)
                .currency(Currency.USD)
                .isActive(true)
                .isDaily(true)
                .koName("엔비디아")
                .usName("NVIDIA")
                .build();
    }

    private Order createBuyOrder(BuyOrderCommand command) {
        return Order.builder()
                .id(1L)
                .memberId(command.memberId())
                .assetId(command.asset().id())
                .category(command.asset().category())
                .currency(command.asset().currency())
                .side(TradeSide.BUY)
                .type(command.orderType())
                .status(OrderStatus.CREATED)
                .quantity(command.quantity())
                .price(command.price())
                .price(command.price())
                .quantity(command.quantity())
                .build();
    }
}
