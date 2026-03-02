package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.CashTransactionDto;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class CashService {

    private final CashCommandService cashCommandService;
    private final CashQueryService cashQueryService;

    @Transactional
    public Long createCashHolding(CreateCashHoldingCommand command) {
        Long cashBalanceId = cashQueryService.getBalanceId(command.userId(), command.currency());

        return cashCommandService.createCashHolding(cashBalanceId, command.amount());
    }

    @Transactional
    public void consumeHoldingAndWithdraw(Long cashHoldingId) {
        cashCommandService.consumeHolding(cashHoldingId);
    }

    public CashBalanceDto getCashBalance(Long userId, Currency currency) {
        CashBalanceEntity balance = cashQueryService.getCashBalance(userId, currency);
        return CashBalanceDto.from(balance);
    }

    @Transactional
    public void performExchange(ExchangeCommand command) {
        // 데드락 방지: 항상 KRW → USD 순서로 락 획득
        CashBalanceEntity krwBalance = cashQueryService.getBalanceWithLock(command.userId(), Currency.KRW);
        CashBalanceEntity usdBalance = cashQueryService.getBalanceWithLock(command.userId(), Currency.USD);

        CashBalanceEntity fromBalance = command.fromCurrency() == Currency.KRW ? krwBalance : usdBalance;
        CashBalanceEntity toBalance = command.toCurrency() == Currency.KRW ? krwBalance : usdBalance;

        fromBalance.withdraw(command.fromAmount());
        toBalance.deposit(command.toAmount());
    }

    public Page<CashTransactionDto> getCashTransactions(
            Long userId,
            Currency currency,
            CashTransactionType transactionType,
            Pageable pageable
    ) {
        Page<CashTransactionEntity> transactions = cashQueryService
                .getCashTransactions(userId, currency, transactionType, pageable);
        return transactions.map(CashTransactionDto::from);
    }
}
