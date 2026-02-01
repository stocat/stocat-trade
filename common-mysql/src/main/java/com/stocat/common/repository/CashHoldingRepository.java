package com.stocat.common.repository;

import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashHoldingRepository extends JpaRepository<CashHoldingEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from CashHoldingEntity h where h.id = :id")
    Optional<CashHoldingEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("select coalesce(sum(h.amount), 0) from CashHoldingEntity h where h.cashBalanceId = :cashBalanceId and h.status = :status")
    BigDecimal sumAmountByCashBalanceIdAndStatus(
            @Param("cashBalanceId") Long cashBalanceId,
            @Param("status") CashHoldingStatus status
    );
}
