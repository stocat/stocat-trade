package com.stocat.tradeapi.cash.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private CashBalanceRepository cashBalanceRepository;
    @Mock
    private CashHoldingRepository cashHoldingRepository;

    @InjectMocks
    private CashService cashService;

    private CashBalanceEntity balance;

    @BeforeEach
    void setUp() {
        balance = CashBalanceEntity.builder()
                .id(1L)
                .userId(99L)
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(1_000))
                .build();
    }

    @Test
    void 잔액이_충분하면_홀딩을_생성한다() {
        CreateCashHoldingCommand command = new CreateCashHoldingCommand(
                balance.getUserId(),
                balance.getCurrency(),
                123L,
                BigDecimal.valueOf(100)
        );
        when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(balance.getUserId(), balance.getCurrency()))
                .thenReturn(Optional.of(balance));
        when(cashHoldingRepository.sumAmountByCashBalanceIdAndStatus(balance.getId(), CashHoldingStatus.HELD))
                .thenReturn(BigDecimal.ZERO);

        cashService.createCashHolding(command);

        ArgumentCaptor<CashHoldingEntity> captor = ArgumentCaptor.forClass(CashHoldingEntity.class);
        verify(cashHoldingRepository).save(captor.capture());
        CashHoldingEntity saved = captor.getValue();
        assertThat(saved.getCashBalanceId()).isEqualTo(balance.getId());
        assertThat(saved.getOrderId()).isEqualTo(123L);
        assertThat(saved.getAmount()).isEqualByComparingTo(command.amount());
    }

    @Test
    void 잔액이_부족하면_예외를_던진다() {
        CreateCashHoldingCommand command = new CreateCashHoldingCommand(
                balance.getUserId(),
                balance.getCurrency(),
                123L,
                BigDecimal.valueOf(2_000)
        );
        when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(balance.getUserId(), balance.getCurrency()))
                .thenReturn(Optional.of(balance));
        when(cashHoldingRepository.sumAmountByCashBalanceIdAndStatus(balance.getId(), CashHoldingStatus.HELD))
                .thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> cashService.createCashHolding(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.INSUFFICIENT_CASH_BALANCE.message());
    }

    @Test
    void 홀딩을_소진하면_잔액을_차감한다() {
        CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), 123L, BigDecimal.valueOf(300));
        when(cashHoldingRepository.findByOrderIdForUpdate(123L)).thenReturn(Optional.of(holding));
        when(cashBalanceRepository.findByIdForUpdate(balance.getId())).thenReturn(Optional.of(balance));

        cashService.consumeHoldingAndWithdraw(123L);

        assertThat(balance.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));
        verify(cashBalanceRepository).save(balance);
        verify(cashHoldingRepository).save(holding);
    }

    @Test
    void 잔액정보가_없으면_예외가_발생한다() {
        CreateCashHoldingCommand command = new CreateCashHoldingCommand(
                balance.getUserId(),
                balance.getCurrency(),
                1L,
                BigDecimal.valueOf(10)
        );
        when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(balance.getUserId(), balance.getCurrency()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashService.createCashHolding(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.CASH_BALANCE_NOT_FOUND.message());
    }

    @Test
    void 금액이_잘못되면_예외가_발생한다() {
        CreateCashHoldingCommand zeroCommand = new CreateCashHoldingCommand(
                balance.getUserId(),
                balance.getCurrency(),
                1L,
                BigDecimal.ZERO
        );

        assertThatThrownBy(() -> cashService.createCashHolding(zeroCommand))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.INVALID_CASH_AMOUNT.message());
    }

    @Test
    void 기존_홀딩으로_가용금액이_부족하면_예외() {
        CreateCashHoldingCommand command = new CreateCashHoldingCommand(
                balance.getUserId(),
                balance.getCurrency(),
                1L,
                BigDecimal.valueOf(700)
        );
        when(cashBalanceRepository.findByUserIdAndCurrencyForUpdate(balance.getUserId(), balance.getCurrency()))
                .thenReturn(Optional.of(balance));
        when(cashHoldingRepository.sumAmountByCashBalanceIdAndStatus(balance.getId(), CashHoldingStatus.HELD))
                .thenReturn(BigDecimal.valueOf(400));

        assertThatThrownBy(() -> cashService.createCashHolding(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.INSUFFICIENT_CASH_BALANCE.message());
    }

    @Test
    void 홀딩이_없으면_소진시_예외() {
        when(cashHoldingRepository.findByOrderIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashService.consumeHoldingAndWithdraw(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.CASH_HOLDING_NOT_FOUND.message());
    }

    @Test
    void 계좌잔액이_부족하면_소진시_예외() {
        CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), 1L, BigDecimal.valueOf(500));
        CashBalanceEntity lowBalance = CashBalanceEntity.builder()
                .id(balance.getId())
                .userId(balance.getUserId())
                .currency(balance.getCurrency())
                .balance(BigDecimal.valueOf(100))
                .build();
        when(cashHoldingRepository.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(holding));
        when(cashBalanceRepository.findByIdForUpdate(balance.getId()))
                .thenReturn(Optional.of(lowBalance));

        assertThatThrownBy(() -> cashService.consumeHoldingAndWithdraw(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.INSUFFICIENT_CASH_BALANCE.message());
    }

    @Test
    void 이미_종료된_홀딩이면_소진시_예외() {
        CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), 1L, BigDecimal.TEN);
        holding.consume();
        when(cashHoldingRepository.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(holding));

        assertThatThrownBy(() -> cashService.consumeHoldingAndWithdraw(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(TradeErrorCode.CASH_HOLDING_ALREADY_FINALIZED.message());
    }
}
