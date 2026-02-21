package com.stocat.tradeapi.cash.controller;

import com.stocat.common.domain.Currency;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.cash.controller.dto.CashBalanceResponse;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.cash.service.dto.CashBalanceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
            @Positive @RequestHeader("X-USER-ID") Long userId,
            @NotNull @RequestParam("currency") Currency currency
    ) {
        CashBalanceDto cashBalance = cashService.getCashBalance(userId, currency);
        CashBalanceResponse response = CashBalanceResponse.from(cashBalance);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
