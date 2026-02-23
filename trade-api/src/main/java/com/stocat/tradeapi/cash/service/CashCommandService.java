package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.common.repository.CashTransactionRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CashCommandService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CashHoldingRepository cashHoldingRepository;
    private final CashTransactionRepository cashTransactionRepository;

    public Long createCashHolding(Long cashBalanceId, BigDecimal amount) {
        validateAmount(amount);

        CashBalanceEntity balance = getBalanceForUpdate(cashBalanceId);
        balance.reserve(amount);

        CashHoldingEntity holding = cashHoldingRepository.save(CashHoldingEntity.hold(balance.getId(), amount));
        return holding.getId();
    }

    public void consumeHolding(Long holdingId) {
        CashHoldingEntity holding = getHoldingForUpdate(holdingId);
        CashBalanceEntity balance = getBalanceForUpdate(holding.getCashBalanceId());

        if (holding.getStatus() != CashHoldingStatus.HOLD) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_ALREADY_FINALIZED);
        }
        holding.consume();

        validateAmount(holding.getAmount());
        validateAmount(balance.getBalance());
        if (balance.getReservedBalance().compareTo(holding.getAmount()) < 0) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }
        balance.settleReservedAmount(holding.getAmount());
        saveTransactionHistory(balance, holding.getAmount(), CashTransactionType.WITHDRAW);
    }

    private void saveTransactionHistory(CashBalanceEntity balance, BigDecimal amount, CashTransactionType type) {
        CashTransactionEntity transaction = CashTransactionEntity.create(
                balance.getUserId(),
                balance.getCurrency(),
                amount,
                balance.getBalance(),
                type
        );
        cashTransactionRepository.save(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(TradeErrorCode.INVALID_CASH_AMOUNT);
        }
    }

    private CashBalanceEntity getBalanceForUpdate(Long cashBalanceId) {
        return cashBalanceRepository.findByIdForUpdate(cashBalanceId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
    }

    private CashHoldingEntity getHoldingForUpdate(Long holdingId) {
        return cashHoldingRepository.findByIdForUpdate(holdingId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND));
    }
}
