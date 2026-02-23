package com.stocat.tradeapi.cash.controller;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.cash.controller.dto.CashBalanceResponse;
import com.stocat.tradeapi.cash.controller.dto.CashTransactionHistoryItemResponse;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import com.stocat.tradeapi.cash.service.dto.CashTransactionDto;
import com.stocat.tradeapi.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/cash")
@Tag(name = "Cash", description = "현금 잔액 조회 API")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @GetMapping("/balance")
    @Operation(
            summary = "보유 현금/주문가능 잔액 조회",
            description = "CashBalanceEntity의 balance, reservedBalance, balance-reserved 값을 모두 반환합니다."
    )
    public ResponseEntity<ApiResponse<CashBalanceResponse>> getCashBalance(
            @Positive @RequestHeader("X-MEMBER-ID") Long userId,
            @NotNull @RequestParam("currency") Currency currency
    ) {
        CashBalanceDto cashBalance = cashService.getCashBalance(userId, currency);
        CashBalanceResponse response = CashBalanceResponse.from(cashBalance);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(summary = "현금 입출금 내역 조회", description = "입금/출금 기록을 최신순으로 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<CashTransactionHistoryItemResponse>>> getCashHistory(
            @Positive @RequestHeader("X-MEMBER-ID") Long userId,
            @NotNull @RequestParam("currency") Currency currency,
            @RequestParam(value = "transactionType", required = false) CashTransactionType transactionType,
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @Min(1) @Max(100) @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CashTransactionDto> history = cashService.getCashTransactions(userId, currency, transactionType,
                pageable);
        PageResponse<CashTransactionHistoryItemResponse> response = new PageResponse<>(
                history.map(CashTransactionHistoryItemResponse::from)
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
