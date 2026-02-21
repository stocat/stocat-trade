package com.stocat.tradeapi.cash.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record CashBalanceResponse(
        @Schema(description = "잔액 통화", example = "USD")
        Currency currency,
        @Schema(description = "전체 보유 현금", example = "1000.00")
        BigDecimal balance,
        @Schema(description = "예약 금액", example = "250.00")
        BigDecimal reservedBalance,
        @Schema(description = "주문 가능 금액", example = "750.00")
        BigDecimal availableAmount
) {
    public static CashBalanceResponse from(CashBalanceDto dto) {
        return new CashBalanceResponse(
                dto.currency(),
                dto.balance(),
                dto.reservedBalance(),
                dto.availableAmount()
        );
    }
}
