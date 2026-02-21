package com.stocat.tradeapi.cash.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exception.TradeErrorCode;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private CashBalanceRepository cashBalanceRepository;
    @Mock
    private CashHoldingRepository cashHoldingRepository;

    private CashService cashService;

    private CashBalanceEntity balance;

    @BeforeEach
    void setUp() {
        CashCommandService commandService = new CashCommandService(cashHoldingRepository);
        CashQueryService queryService = new CashQueryService(cashBalanceRepository, cashHoldingRepository);
        cashService = new CashService(commandService, queryService);

        balance = CashBalanceEntity.builder()
                .id(1L)
                .userId(99L)
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(1_000))
                .reservedBalance(BigDecimal.ZERO)
                .build();
    }

    @Nested
    @DisplayName("현금 홀딩 생성 검증")
    class CreateCashHolding {

        @Test
        void 잔액이_충분하면_홀딩을_생성한다() {
            CreateCashHoldingCommand command = new CreateCashHoldingCommand(99L, Currency.USD, BigDecimal.valueOf(100));
            when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(99L, Currency.USD))
                    .thenReturn(Optional.of(balance));
            when(cashHoldingRepository.save(any(CashHoldingEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            CashHoldingEntity result = cashService.createCashHolding(command);

            assertThat(balance.getReservedBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
            assertThat(result.getCashBalanceId()).isEqualTo(balance.getId());
        }

        @Test
        void 잔액이_부족하면_예외를_던진다() {
            CreateCashHoldingCommand command = new CreateCashHoldingCommand(99L, Currency.USD,
                    BigDecimal.valueOf(1_500));
            when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(99L, Currency.USD))
                    .thenReturn(Optional.of(balance));

            assertThatThrownBy(() -> cashService.createCashHolding(command))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining(TradeErrorCode.INSUFFICIENT_CASH_BALANCE.message());

            assertThat(balance.getReservedBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void 계좌_정보가_존재하지_않으면_예외가_발생한다() {
            when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(99L, Currency.USD))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashService.createCashHolding(
                    new CreateCashHoldingCommand(99L, Currency.USD, BigDecimal.TEN)))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining(TradeErrorCode.CASH_BALANCE_NOT_FOUND.message());
        }
    }

    @Nested
    @DisplayName("홀딩 소진 및 출금 검증")
    class ConsumeHolding {

        @Test
        void 홀딩을_소진하면_전체_잔액과_예약_잔액을_차감한다() {
            // Given: 미리 300원이 예약된 상태
            balance.reserve(BigDecimal.valueOf(300));
            CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), BigDecimal.valueOf(300));

            when(cashHoldingRepository.findByIdForUpdate(123L)).thenReturn(Optional.of(holding));
            when(cashBalanceRepository.findByIdForUpdate(balance.getId())).thenReturn(Optional.of(balance));

            // When
            cashService.consumeHoldingAndWithdraw(123L);

            // Then: 전체 잔액 1000 -> 700, 예약금 300 -> 0
            assertThat(balance.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));
            assertThat(balance.getReservedBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void 이미_종료된_홀딩이면_재소진시_예외() {
            // Given
            BigDecimal amount = BigDecimal.TEN;
            balance.reserve(amount);
            CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), amount);
            holding.consume();

            when(cashHoldingRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(holding));
            when(cashBalanceRepository.findByIdForUpdate(balance.getId())).thenReturn(Optional.of(balance));

            // When & Then
            assertThatThrownBy(() -> cashService.consumeHoldingAndWithdraw(1L))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining(TradeErrorCode.INSUFFICIENT_CASH_BALANCE.message());
        }
    }

    @Nested
    @DisplayName("잔액 조회 검증")
    class GetBalance {

        @Test
        void 유저의_현금_잔액을_조회한다() {
            balance.reserve(BigDecimal.valueOf(200));
            when(cashBalanceRepository.findByUserIdAndCurrency(99L, Currency.USD))
                    .thenReturn(Optional.of(balance));

            CashBalanceDto result = cashService.getCashBalance(99L, Currency.USD);
            
            assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(1_000));
            assertThat(result.availableAmount()).isEqualByComparingTo(BigDecimal.valueOf(800));
            assertThat(result.currency()).isEqualTo(Currency.USD);
        }
    }
}
