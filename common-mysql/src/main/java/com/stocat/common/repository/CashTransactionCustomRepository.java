package com.stocat.common.repository;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CashTransactionCustomRepository {
    Page<CashTransactionEntity> findTransactions(Long userId, Currency currency, CashTransactionType type,
                                                 Pageable pageable);
}
