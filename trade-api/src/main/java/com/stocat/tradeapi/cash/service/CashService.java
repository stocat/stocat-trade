package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashCommandService cashCommandService;
    private final CashQueryService cashQueryService;

    public CashHoldingEntity createCashHolding(CreateCashHoldingCommand command) {
        CashBalanceEntity balance = cashQueryService.getBalanceForUpdate(command.userId(), command.currency());

        return cashCommandService.createCashHolding(balance, command.amount());
    }

    public void consumeHoldingAndWithdraw(Long cashHoldingId) {
        CashHoldingEntity holding = cashQueryService.getHoldingForUpdate(cashHoldingId);
        CashBalanceEntity balance = cashQueryService.getBalanceForUpdate(holding.getCashBalanceId());

        cashCommandService.consumeHolding(holding, balance);
    }

    public CashBalanceDto getCashBalance(Long userId, Currency currency) {
        CashBalanceEntity balance = cashQueryService.getCashBalance(userId, currency);
        return CashBalanceDto.from(balance);
    }
}
