package com.stocat.tradeapi.exchange.service.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.exchange.ExchangeHistoryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeHistoryDto(
        Long id,
        Currency fromCurrency,
        Currency toCurrency,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal exchangeRate,
        LocalDateTime exchangedAt
) {

    public static ExchangeHistoryDto from(ExchangeHistoryEntity entity) {
        return new ExchangeHistoryDto(
                entity.getId(),
                entity.getFromCurrency(),
                entity.getToCurrency(),
                entity.getFromAmount(),
                entity.getToAmount(),
                entity.getExchangeRate(),
                entity.getCreatedAt()
        );
    }
}