package com.stocat.common.domain.cash;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "cash_holdings")
public class CashHoldingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cash_balance_id", nullable = false)
    private Long cashBalanceId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CashHoldingStatus status;

    public static CashHoldingEntity hold(Long cashBalanceId, Long orderId, BigDecimal amount) {
        if (cashBalanceId == null) {
            throw new IllegalArgumentException("cash balance id is required");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return CashHoldingEntity.builder()
                .cashBalanceId(cashBalanceId)
                .orderId(orderId)
                .amount(amount)
                .status(CashHoldingStatus.HELD)
                .build();
    }

    public void consume() {
        if (status != CashHoldingStatus.HELD) { // HELD 상태에서만 변경 가능
            throw new IllegalStateException("holding already finalized");
        }
        status = CashHoldingStatus.CONSUMED;
    }
}
