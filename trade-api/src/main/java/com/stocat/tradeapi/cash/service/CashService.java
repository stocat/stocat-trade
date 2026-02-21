package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.CashTransactionHistoryDto;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
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
    public CashHoldingEntity createCashHolding(CreateCashHoldingCommand command) {
        CashBalanceEntity balance = cashQueryService.getBalanceForUpdate(command.userId(), command.currency());

        return cashCommandService.createCashHolding(balance, command.amount());
    }

    @Transactional
    public void consumeHoldingAndWithdraw(Long cashHoldingId) {
        CashHoldingEntity holding = cashQueryService.getHoldingForUpdate(cashHoldingId);
        CashBalanceEntity balance = cashQueryService.getBalanceForUpdate(holding.getCashBalanceId());

        cashCommandService.consumeHolding(holding, balance);
    }

    public CashBalanceDto getCashBalance(Long userId, Currency currency) {
        CashBalanceEntity balance = cashQueryService.getCashBalance(userId, currency);
        return CashBalanceDto.from(balance);
    }

    public CashTransactionHistoryDto getCashTransactions(
            Long userId,
            Currency currency,
            CashTransactionType transactionType,
            Pageable pageable
    ) {
        Page<CashTransactionEntity> histories = cashQueryService
                .getCashTransactions(userId, currency, transactionType, pageable);
        return CashTransactionHistoryDto.from(histories);
    }
}
