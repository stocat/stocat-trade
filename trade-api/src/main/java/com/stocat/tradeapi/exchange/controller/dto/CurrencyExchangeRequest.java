package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.tradeapi.exchange.usecase.dto.CurrencyExchangeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CurrencyExchangeRequest(
        @NotNull
        @Schema(description = "출금 통화", example = "KRW | USD")
        Currency fromCurrency,

        @NotNull
        @Schema(description = "입금 통화", example = "KRW | USD")
        Currency toCurrency,

        @NotNull @Positive
        @Schema(description = "환전할 금액", example = "1350000")
        BigDecimal fromAmount
) {

    public CurrencyExchangeCommand toCommand(Long userId) {
        return new CurrencyExchangeCommand(userId, fromCurrency, toCurrency, fromAmount);
    }
}