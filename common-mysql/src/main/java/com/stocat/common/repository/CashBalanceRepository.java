package com.stocat.common.repository;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashBalanceRepository extends JpaRepository<CashBalanceEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CashBalanceEntity c where c.userId = :userId and c.currency = :currency")
    Optional<CashBalanceEntity> findByUserIdAndCurrencyForUpdate(
            @Param("userId") Long userId,
            @Param("currency") Currency currency
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CashBalanceEntity c where c.id = :id")
    Optional<CashBalanceEntity> findByIdForUpdate(@Param("id") Long id);
}
