package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.tradeapi.exchange.service.dto.ExchangePreviewDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ExchangePreviewResponse(
        @Schema(description = "환전 출금 금액", example = "1350000.00000000")
        BigDecimal fromAmount,

        @Schema(description = "환전 예상 수취 금액", example = "1000.00000000")
        BigDecimal toAmount,

        @Schema(description = "적용 환율", example = "1350.00000000")
        BigDecimal exchangeRate,

        @Schema(description = "환율 고정 키 (환전 요청 시 사용)")
        String rateLockKey,

        @Schema(description = "환율 고정 만료 시간 (초)", example = "30")
        long expiresIn
) {
    public static ExchangePreviewResponse from(ExchangePreviewDto dto) {
        return new ExchangePreviewResponse(
                dto.fromAmount(), dto.toAmount(), dto.exchangeRate(), dto.rateLockKey(), dto.expiresIn());
    }
}