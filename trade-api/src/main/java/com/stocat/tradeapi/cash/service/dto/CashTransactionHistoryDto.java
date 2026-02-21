package com.stocat.tradeapi.cash.service.dto;

import com.stocat.common.domain.cash.CashTransactionEntity;
import java.util.List;
import org.springframework.data.domain.Page;

public record CashTransactionHistoryDto(
        List<CashTransactionDto> transactions,
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean hasNext
) {

    public static CashTransactionHistoryDto from(Page<CashTransactionEntity> pageResult) {
        List<CashTransactionDto> transactions = pageResult.getContent().stream()
                .map(CashTransactionDto::from)
                .toList();
        return new CashTransactionHistoryDto(
                transactions,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.hasNext()
        );
    }
}
