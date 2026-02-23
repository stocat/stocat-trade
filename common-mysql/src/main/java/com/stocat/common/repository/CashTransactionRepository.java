package com.stocat.common.repository;

import com.stocat.common.domain.cash.CashTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashTransactionRepository extends JpaRepository<CashTransactionEntity, Long>,
        CashTransactionCustomRepository {
}
