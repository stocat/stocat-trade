package com.stocat.tradeapi.cash.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CashHoldingRepository cashHoldingRepository;

    @Transactional
    public CashHoldingEntity createCashHolding(CreateCashHoldingCommand command) {
        validateAmount(command.amount());

        CashBalanceEntity balance = cashBalanceRepository
                .findByUserIdAndCurrencyForUpdate(command.userId(), command.currency())
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));

        try {
            balance.reserve(command.amount());
        } catch (IllegalStateException ex) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }

        CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), command.amount());
        return cashHoldingRepository.save(holding);
    }

    @Transactional
    public void consumeHoldingAndWithdraw(Long cashHoldingId) {
        if (cashHoldingId == null) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND);
        }
        CashHoldingEntity holding = cashHoldingRepository
                .findByIdForUpdate(cashHoldingId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND));

        try {
            holding.consume();
        } catch (IllegalStateException ex) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_ALREADY_FINALIZED);
        }

        CashBalanceEntity balance = cashBalanceRepository
                .findByIdForUpdate(holding.getCashBalanceId())
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));

        try {
            balance.settleReservedAmount(holding.getAmount());
        } catch (IllegalStateException ex) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }
    }

    @Transactional(readOnly = true)
    public CashBalanceDto getCashBalance(Long userId, Currency currency) {
        CashBalanceEntity balance = cashBalanceRepository
                .findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
        return CashBalanceDto.from(balance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(TradeErrorCode.INVALID_CASH_AMOUNT);
        }
    }
}
