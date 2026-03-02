package com.stocat.tradeapi.exchange.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ExchangePreviewResponse(
        @Schema(description = "입금 예상액", example = "1000.00000000")
        BigDecimal toAmount,

        @Schema(description = "적용 환율", example = "1350.00000000")
        BigDecimal exchangeRate
) {
}