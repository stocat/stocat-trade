package com.stocat.common.domain.cash;

import com.stocat.common.domain.BaseEntity;
import com.stocat.common.domain.Currency;
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
@Table(name = "cash_balances")
public class CashBalanceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Currency currency;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "reserved_balance", nullable = false)
    @Builder.Default
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    public void reserve(BigDecimal amount) {
        requirePositive(amount);
        BigDecimal available = balance.subtract(reservedBalance);
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient available balance to reserve");
        }
        reservedBalance = reservedBalance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient cash balance");
        }
        balance = balance.subtract(amount);

        // 예약금에서 실제 출금으로 이어지는 경우 예약금도 함께 차감
        if (reservedBalance.compareTo(amount) >= 0) {
            reservedBalance = reservedBalance.subtract(amount);
        }
    }

    public void cancelReservation(BigDecimal amount) {
        requirePositive(amount);
        if (reservedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("cannot cancel more than reserved balance");
        }
        reservedBalance = reservedBalance.subtract(amount);
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
