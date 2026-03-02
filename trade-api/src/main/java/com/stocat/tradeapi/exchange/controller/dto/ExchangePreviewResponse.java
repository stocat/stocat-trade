package com.stocat.tradeapi.exchange.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ExchangePreviewResponse(
        @Schema(description = "환전 예상 금액", example = "1000.00000000")
        BigDecimal toAmount,

        @Schema(description = "적용 환율", example = "1350.00000000")
        BigDecimal exchangeRate,

        @Schema(description = "환율 고정 키 (환전 요청 시 사용)")
        String rateLockKey,

        @Schema(description = "환율 고정 만료 시간 (초)", example = "30")
        long expiresIn
) {
}