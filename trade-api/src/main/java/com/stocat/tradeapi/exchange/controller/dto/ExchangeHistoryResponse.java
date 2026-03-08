package com.stocat.tradeapi.exchange.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeHistoryResponse(
        @Schema(description = "환전 내역 ID", example = "1")
        Long id,

        @Schema(description = "출금 통화", example = "KRW")
        Currency fromCurrency,

        @Schema(description = "입금 통화", example = "USD")
        Currency toCurrency,

        @Schema(description = "출금액", example = "1350000")
        BigDecimal fromAmount,

        @Schema(description = "입금액", example = "1000.00000000")
        BigDecimal toAmount,

        @Schema(description = "적용 환율", example = "1350.00000000")
        BigDecimal exchangeRate,

        @Schema(description = "환전 시각")
        LocalDateTime exchangedAt
) {

    public static ExchangeHistoryResponse from(ExchangeHistoryDto dto) {
        return new ExchangeHistoryResponse(
                dto.id(),
                dto.fromCurrency(),
                dto.toCurrency(),
                dto.fromAmount(),
                dto.toAmount(),
                dto.exchangeRate(),
                dto.exchangedAt()
        );
    }
}