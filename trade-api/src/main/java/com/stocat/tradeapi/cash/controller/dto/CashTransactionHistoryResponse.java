package com.stocat.tradeapi.cash.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.tradeapi.cash.service.dto.CashTransactionDto;
import com.stocat.tradeapi.cash.service.dto.CashTransactionHistoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CashTransactionHistoryResponse(
        @Schema(description = "입출금 내역 목록")
        List<CashTransactionItem> transactions,
        @Schema(description = "전체 데이터 건수")
        long totalElements,
        @Schema(description = "전체 페이지 수")
        int totalPages,
        @Schema(description = "현재 페이지 (0부터 시작)")
        int page,
        @Schema(description = "페이지 당 조회 건수")
        int size,
        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
) {

    public static CashTransactionHistoryResponse from(CashTransactionHistoryDto dto) {
        List<CashTransactionItem> items = dto.transactions().stream()
                .map(CashTransactionItem::from)
                .toList();
        return new CashTransactionHistoryResponse(
                items,
                dto.totalElements(),
                dto.totalPages(),
                dto.page(),
                dto.size(),
                dto.hasNext()
        );
    }

    public record CashTransactionItem(
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
        public static CashTransactionItem from(CashTransactionDto dto) {
            return new CashTransactionItem(
                    dto.transactionId(),
                    dto.currency(),
                    dto.transactionType(),
                    dto.amount(),
                    dto.balanceAfter(),
                    dto.transactedAt()
            );
        }
    }
}
