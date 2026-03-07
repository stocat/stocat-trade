package com.stocat.tradeapi.order.usecase;

import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrder;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createBuyOrderCommand;
import static com.stocat.tradeapi.order.OrderFixtureUtils.createUsdAssetDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.stocat.common.domain.order.Order;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.infrastructure.matchapi.MatchApiClient;
import com.stocat.tradeapi.infrastructure.quoteapi.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderCommandService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyOrderFacadeTest {

    @Mock
    private CashService cashService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private MatchApiClient matchApiClient;

    @InjectMocks
    private BuyOrderFacade buyOrderFacade;

    @Test
    void 주문처리시_현금홀딩과_주문생성을_순차적으로_호출한다() {
        // Given
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        Order order = createBuyOrder(command);

        given(cashService.createCashHolding(any(CreateCashHoldingCommand.class))).willReturn(1L);
        given(orderCommandService.createBuyOrder(command, asset, 1L)).willReturn(order);

        // When
        OrderDto result = buyOrderFacade.processBuyOrder(command, asset);

        // Then
        assertThat(result.id()).isEqualTo(order.getId());
        verify(cashService).createCashHolding(any(CreateCashHoldingCommand.class));
        verify(orderCommandService).createBuyOrder(command, asset, 1L);
    }

    @Test
    void 주문처리시_외부API는_절대_호출하지_않는다() {
        // Given
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();
        Order order = createBuyOrder(command);

        given(cashService.createCashHolding(any())).willReturn(1L);
        given(orderCommandService.createBuyOrder(any(), any(), any())).willReturn(order);

        // When
        buyOrderFacade.processBuyOrder(command, asset);

        // Then (트랜잭션 안에서 외부 호출이 없음)
        verify(matchApiClient, never()).submitBuyOrder(any());
        verify(matchApiClient, never()).cancelOrder(any());
    }

    @Test
    void 현금홀딩_실패시_예외가_전파된다() {
        // Given
        BuyOrderCommand command = createBuyOrderCommand();
        AssetDto asset = createUsdAssetDto();

        given(cashService.createCashHolding(any()))
                .willThrow(new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE));

        // When & Then
        assertThatThrownBy(() -> buyOrderFacade.processBuyOrder(command, asset))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", TradeErrorCode.INSUFFICIENT_CASH_BALANCE);

        // 주문 생성은 호출되지 않아야 함
        verify(orderCommandService, never()).createBuyOrder(any(), any(), any());
    }
}
