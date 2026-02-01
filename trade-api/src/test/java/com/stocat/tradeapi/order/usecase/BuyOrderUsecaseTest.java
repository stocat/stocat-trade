package com.stocat.tradeapi.order.usecase;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrderCommand;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createUsdAssetDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import com.stocat.common.domain.order.Order;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.common.repository.OrderRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.matchapi.dto.BuyOrderSubmissionResponse;
import com.stocat.tradeapi.infrastructure.quoteapi.QuoteApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(BuyOrderUsecaseTest.MockConfig.class)
class BuyOrderUsecaseTest {

    @Autowired
    private BuyOrderUsecase buyOrderUsecase;
    @Autowired
    private CashBalanceRepository cashBalanceRepository;
    @Autowired
    private CashHoldingRepository cashHoldingRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QuoteApiClient quoteApiClient;
    @Autowired
    private MatchApiClient matchApiClient;

    @BeforeEach
    void cleanUp() {
        cashHoldingRepository.deleteAll();
        orderRepository.deleteAll();
        cashBalanceRepository.deleteAll();
    }

    @Test
    void 매수주문이_성공하면_주문과_현금홀딩이_생성된다() {
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        prepareCashBalance(command.userId(), asset.currency(), BigDecimal.valueOf(100_000));

        given(quoteApiClient.fetchAsset(command.assetSymbol())).willReturn(asset);
        given(matchApiClient.submitBuyOrder(any())).willReturn(new BuyOrderSubmissionResponse("success"));

        OrderDto orderDto = buyOrderUsecase.placeBuyOrder(command);

        Order persistedOrder = orderRepository.findById(orderDto.id()).orElseThrow();
        assertThat(persistedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(persistedOrder.getUserId()).isEqualTo(command.userId());

        List<CashHoldingEntity> holdings = cashHoldingRepository.findAll();
        assertThat(holdings).hasSize(1);
        CashHoldingEntity holding = holdings.getFirst();
        assertThat(persistedOrder.getCashHoldingId()).isEqualTo(holding.getId());
        assertThat(orderDto.cashHoldingId()).isEqualTo(holding.getId());
        assertThat(holding.getAmount()).isEqualByComparingTo(command.price().multiply(command.quantity()));
        assertThat(holding.getStatus()).isEqualTo(CashHoldingStatus.HELD);
    }

    @Test
    void 자산조회실패시_DB에_변경사항이_없다() {
        BuyOrderCommand command = createBuyOrderCommand();
        given(quoteApiClient.fetchAsset(command.assetSymbol())).willReturn(null);

        assertThatThrownBy(() -> buyOrderUsecase.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.ASSET_NOT_FOUND);

        assertThat(orderRepository.count()).isZero();
        assertThat(cashHoldingRepository.count()).isZero();
    }

    @Test
    void 현금홀딩실패시_주문도_롤백된다() {
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        prepareCashBalance(command.userId(), asset.currency(), BigDecimal.valueOf(100));

        given(quoteApiClient.fetchAsset(command.assetSymbol())).willReturn(asset);
        given(matchApiClient.submitBuyOrder(any())).willReturn(new BuyOrderSubmissionResponse("success"));

        assertThatThrownBy(() -> buyOrderUsecase.placeBuyOrder(command))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INSUFFICIENT_CASH_BALANCE);

        assertThat(orderRepository.count()).isZero();
        assertThat(cashHoldingRepository.count()).isZero();
    }

    private void prepareCashBalance(Long userId, Currency currency, BigDecimal amount) {
        cashBalanceRepository.save(CashBalanceEntity.builder()
                .userId(userId)
                .currency(currency)
                .balance(amount)
                .build());
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
