package com.stocat.tradeapi.cash.service.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import java.math.BigDecimal;

public record CashBalanceDto(
        Long id,
        Long userId,
        Currency currency,
        BigDecimal balance,
        BigDecimal reservedBalance,
        BigDecimal availableAmount
) {

    public static CashBalanceDto from(CashBalanceEntity entity) {
        BigDecimal currentBalance = normalize(entity.getBalance());
        BigDecimal currentReserved = normalize(entity.getReservedBalance());
        BigDecimal available = currentBalance.subtract(currentReserved);
        return new CashBalanceDto(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrency(),
                currentBalance,
                currentReserved,
                available
        );
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
