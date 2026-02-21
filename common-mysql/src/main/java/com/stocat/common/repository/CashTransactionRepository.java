package com.stocat.common.repository;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashTransactionRepository extends JpaRepository<CashTransactionEntity, Long> {

    @Query("SELECT c FROM CashTransactionEntity c " +
            "WHERE c.userId = :userId AND c.currency = :currency " +
            "AND (:type IS NULL OR c.transactionType = :type)")
    Page<CashTransactionEntity> findTransactions(
            @Param("userId") Long userId,
            @Param("currency") Currency currency,
            @Param("type") CashTransactionType type,
            Pageable pageable
    );
}
