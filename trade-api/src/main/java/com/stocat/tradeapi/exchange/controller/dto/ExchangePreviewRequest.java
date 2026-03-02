package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.tradeapi.exchange.service.dto.ExchangePreviewQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ExchangePreviewRequest(
        @NotNull
        @Schema(description = "출금 통화", example = "KRW")
        Currency fromCurrency,

        @NotNull
        @Schema(description = "수취 통화", example = "USD")
        Currency toCurrency,

        @Positive
        @Schema(description = "출금 금액 (toAmount와 택 1)", example = "1350000")
        BigDecimal fromAmount,

        @Positive
        @Schema(description = "수취 금액 (fromAmount와 택 1)", example = "1000")
        BigDecimal toAmount
) {
    public ExchangePreviewQuery toQuery() {
        return new ExchangePreviewQuery(fromCurrency, toCurrency, fromAmount, toAmount);
    }
}