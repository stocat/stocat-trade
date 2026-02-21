package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.common.repository.CashTransactionRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashQueryService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CashHoldingRepository cashHoldingRepository;
    private final CashTransactionRepository cashTransactionRepository;

    public CashBalanceEntity getCashBalance(Long userId, Currency currency) {
        return cashBalanceRepository
                .findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
    }

    public CashBalanceEntity getBalanceForUpdate(Long balanceId) {
        return cashBalanceRepository.findByIdForUpdate(balanceId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
    }

    public CashBalanceEntity getBalanceForUpdate(Long userId, Currency currency) {
        return cashBalanceRepository.findByUserIdAndCurrencyForUpdate(userId, currency)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
    }

    public CashHoldingEntity getHoldingForUpdate(Long id) {
        if (id == null) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND);
        }
        return cashHoldingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND));
    }

    public Page<CashTransactionEntity> getCashTransactions(
            Long userId,
            Currency currency,
            CashTransactionType transactionType,
            Pageable pageable
    ) {
        return cashTransactionRepository
                .findTransactions(userId, currency, transactionType, pageable);
    }
}
