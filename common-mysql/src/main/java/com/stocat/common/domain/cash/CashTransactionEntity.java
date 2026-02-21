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
@Table(name = "cash_transactions")
public class CashTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private CashTransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    public static CashTransactionEntity create(
            Long userId,
            com.stocat.common.domain.Currency currency,
            BigDecimal amount,
            BigDecimal balanceAfter,
            CashTransactionType transactionType
    ) {
        return CashTransactionEntity.builder()
                .userId(userId)
                .currency(currency)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .transactionType(transactionType)
                .build();
    }
}
