package com.stocat.tradeapi.order.usecase;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createSellOrderCommand;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createUsdAssetDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.OrderRepository;
import com.stocat.common.repository.PositionRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.SellOrderSubmissionRequest;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SellOrderUsecaseTest {

    @Autowired
    private SellOrderUsecase sellOrderUsecase;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private QuoteApiClient quoteApiClient;
    @Autowired
    private MatchApiClient matchApiClient;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        positionRepository.deleteAll();
        Mockito.reset(quoteApiClient, matchApiClient);
    }

    @Test
    void 매도주문이_성공하면_SELL_주문과_외부API호출이_발생한다() {
        SellOrderCommand command = createSellOrderCommand();
        AssetDto asset = createUsdAssetDto();
        positionRepository.save(PositionEntity.create(command.userId(), asset.id(), BigDecimal.valueOf(100), BigDecimal.valueOf(150)));

        given(quoteApiClient.fetchAsset(command.assetSymbol())).willReturn(asset);

        OrderDto dto = sellOrderUsecase.placeSellOrder(command);

        assertThat(dto.side()).isEqualTo(TradeSide.SELL);

        Order persistedOrder = orderRepository.findById(dto.id()).orElseThrow();
        assertThat(persistedOrder.getSide()).isEqualTo(TradeSide.SELL);
        assertThat(persistedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(matchApiClient, times(1)).submitSellOrder(any(SellOrderSubmissionRequest.class));
    }

    @Test
    void 보유수량보다_많이_매도요청하면_예외가_발생한다() {
        SellOrderCommand command = createSellOrderCommand();
        AssetDto asset = createUsdAssetDto();
        positionRepository.save(PositionEntity.create(command.userId(), asset.id(), BigDecimal.ONE, BigDecimal.valueOf(150)));
        given(quoteApiClient.fetchAsset(command.assetSymbol())).willReturn(asset);

        assertThatThrownBy(() -> sellOrderUsecase.placeSellOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INSUFFICIENT_POSITION_QUANTITY);

        assertThat(orderRepository.count()).isZero();
        verify(matchApiClient, times(0)).submitSellOrder(any());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        QuoteApiClient quoteApiClient() {
            return Mockito.mock(QuoteApiClient.class);
        }

        @Bean
        @Primary
        MatchApiClient matchApiClient() {
            return Mockito.mock(MatchApiClient.class);
        }
    }
}
