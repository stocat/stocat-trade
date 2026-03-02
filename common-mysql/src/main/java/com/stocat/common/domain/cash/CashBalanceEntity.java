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

    public void settleReservedAmount(BigDecimal amount) {
        requirePositive(amount);
        if (reservedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("cannot settle more than reserved balance");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient cash balance");
        }
        balance = balance.subtract(amount);
        reservedBalance = reservedBalance.subtract(amount);
    }

    public void cancelReservation(BigDecimal amount) {
        requirePositive(amount);
        if (reservedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("cannot cancel more than reserved balance");
        }
        reservedBalance = reservedBalance.subtract(amount);
    }

    /**
     * 가용 잔고(balance - reservedBalance)에서 즉시 출금합니다.
     *
     * @param amount 출금할 금액
     */
    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        BigDecimal available = balance.subtract(reservedBalance);
        if (available.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient available balance to withdraw");
        }
        balance = balance.subtract(amount);
    }

    /**
     * 잔고를 즉시 증액합니다.
     *
     * @param amount 입금할 금액
     */
    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
