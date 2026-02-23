package com.stocat.tradeapi.cash.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.tradeapi.cash.service.dto.CashTransactionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashTransactionHistoryItemResponse(
        @Schema(description = "거래 ID", example = "101")
        Long transactionId,
        @Schema(description = "잔액 통화", example = "USD")
        Currency currency,
        @Schema(description = "거래 종류", example = "DEPOSIT")
        CashTransactionType transactionType,
        @Schema(description = "거래 금액", example = "1000.00")
        BigDecimal amount,
        @Schema(description = "거래 이후 잔액", example = "11000.00")
        BigDecimal balanceAfter,
        @Schema(description = "거래 시각")
        LocalDateTime transactedAt
) {
    public static CashTransactionHistoryItemResponse from(CashTransactionDto dto) {
        return new CashTransactionHistoryItemResponse(
                dto.transactionId(),
                dto.currency(),
                dto.transactionType(),
                dto.amount(),
                dto.balanceAfter(),
                dto.transactedAt()
        );
    }
}
