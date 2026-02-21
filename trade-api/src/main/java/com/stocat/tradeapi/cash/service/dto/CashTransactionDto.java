package com.stocat.tradeapi.cash.service.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashTransactionDto(
        Long transactionId,
        Long userId,
        Currency currency,
        CashTransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime transactedAt
) {

    public static CashTransactionDto from(CashTransactionEntity entity) {
        return new CashTransactionDto(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrency(),
                entity.getTransactionType(),
                entity.getAmount(),
                entity.getBalanceAfter(),
                entity.getCreatedAt()
        );
    }
}
