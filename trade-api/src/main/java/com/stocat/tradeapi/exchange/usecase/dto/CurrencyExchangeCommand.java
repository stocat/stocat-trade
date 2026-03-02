package com.stocat.tradeapi.exchange.usecase.dto;

public record CurrencyExchangeCommand(
        Long userId,
        String rateLockKey
) {
}