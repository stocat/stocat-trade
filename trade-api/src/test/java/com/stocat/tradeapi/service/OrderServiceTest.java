package com.stocat.tradeapi.service;

import com.stocat.common.domain.AssetsCategory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.event.BuyOrderCreatedEvent;
import com.stocat.tradeapi.infrastructure.MatchApiClient;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.service.dto.OrderDto;
import com.stocat.tradeapi.service.dto.command.BuyOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderQueryService, orderCommandService, matchApiClient, eventPublisher);
    }

    @Test
    void 매수요청시_주문생성은_OrderCommandService에_위임한다() {
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

    private BuyOrderCommand createBuyOrderCommand() {
        return BuyOrderCommand.builder()
                .memberId(1L)
                .orderType(OrderType.LIMIT)
                .asset(createAssetDto())
                .price(BigDecimal.valueOf(200))
                .quantity(BigDecimal.valueOf(100))
                .build();
    }

    private AssetDto createAssetDto() {
        return AssetDto.builder()
                .id(1)
                .symbol("NVDA")
                .category(AssetsCategory.USD)
                .currency(Currency.USD)
                .isDaily(true)
                .isDaily(true)
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
