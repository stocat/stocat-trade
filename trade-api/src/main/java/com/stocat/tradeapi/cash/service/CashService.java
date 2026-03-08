package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.CashTransactionDto;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import java.util.List;
import java.util.Map;
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
        // 데드락 방지: ORDER BY currency ASC로 DB가 항상 동일한 순서로 락 획득
        Map<Currency, CashBalanceEntity> balances = cashQueryService
                .getBalancesWithLock(command.userId(), List.of(Currency.KRW, Currency.USD));

        balances.get(command.fromCurrency()).withdraw(command.fromAmount());
        balances.get(command.toCurrency()).deposit(command.toAmount());
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
