package com.stocat.tradeapi.infrastructure.matchapi;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionResponse;
import com.stocat.tradeapi.order.event.OrderPlacedEvent;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.usecase.SellOrderFacade;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MatchApiFacadeTest.TestConfig.class)
class MatchApiFacadeTest {

    @Autowired
    private MatchApiFacade matchApiFacade;
    @Autowired
    private MatchApiClient matchApiClient;
    @Autowired
    private SellOrderFacade sellOrderFacade;

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        Mockito.reset(matchApiClient, sellOrderFacade);
        orderDto = OrderDto.builder()
                .id(1L)
                .userId(10L)
                .assetId(100L)
                .cashHoldingId(200L)
                .side(TradeSide.SELL)
                .type(OrderType.LIMIT)
                .status(OrderStatus.PENDING)
                .quantity(BigDecimal.valueOf(50))
                .price(BigDecimal.valueOf(250))
                .tif(OrderTif.GTC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 매칭API가_3회_실패하면_주문취소가_호출된다() {
        given(matchApiClient.submitSellOrder(any(SellOrderSubmissionRequest.class)))
                .willThrow(new RuntimeException("match api down"));

        OrderPlacedEvent event = new OrderPlacedEvent(orderDto);

        assertThatCode(() -> matchApiFacade.submitSellOrderWithRetry(event))
                .doesNotThrowAnyException();

        verify(matchApiClient, times(3)).submitSellOrder(any(SellOrderSubmissionRequest.class));
        verify(sellOrderFacade, times(1)).compensateSellOrder(eq(event.orderDto().id()));
    }

    @Test
    void 매칭API가_재시도내에_성공하면_주문취소가_발생하지_않는다() {
        given(matchApiClient.submitSellOrder(any(SellOrderSubmissionRequest.class)))
                .willThrow(new RuntimeException("network glitch"))
                .willThrow(new RuntimeException("network glitch again"))
                .willReturn(new SellOrderSubmissionResponse("success"));

        OrderPlacedEvent event = new OrderPlacedEvent(orderDto);

        assertThatCode(() -> matchApiFacade.submitSellOrderWithRetry(event))
                .doesNotThrowAnyException();

        verify(matchApiClient, times(3)).submitSellOrder(any(SellOrderSubmissionRequest.class));
        verify(sellOrderFacade, never()).compensateSellOrder(eq(event.orderDto().id()));
    }

    @Configuration
    @EnableRetry
    static class TestConfig {
        @Bean
        MatchApiFacade matchApiFacade(MatchApiClient matchApiClient, SellOrderFacade sellOrderFacade) {
            return new MatchApiFacade(matchApiClient, sellOrderFacade);
        }

        @Bean
        @Primary
        MatchApiClient matchApiClient() {
            return Mockito.mock(MatchApiClient.class);
        }

        @Bean
        @Primary
        SellOrderFacade sellOrderFacade() {
            return Mockito.mock(SellOrderFacade.class);
        }
    }
}
