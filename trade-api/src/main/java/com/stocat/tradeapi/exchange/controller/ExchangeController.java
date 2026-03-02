package com.stocat.tradeapi.exchange.controller;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.common.dto.PageResponse;
import com.stocat.tradeapi.exchange.controller.dto.CurrencyExchangeRequest;
import com.stocat.tradeapi.exchange.controller.dto.ExchangeHistoryResponse;
import com.stocat.tradeapi.exchange.service.ExchangeHistoryService;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.usecase.CurrencyExchangeUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Validated
@RestController
@RequestMapping("/exchange")
@Tag(name = "Exchange", description = "환전 API")
@RequiredArgsConstructor
public class ExchangeController {

    private final CurrencyExchangeUsecase currencyExchangeUsecase;
    private final ExchangeHistoryService exchangeHistoryService;

    @PostMapping
    @Operation(summary = "환전 실행")
    public ResponseEntity<ApiResponse<ExchangeHistoryResponse>> exchange(
            @Positive @RequestHeader("X-MEMBER-ID") Long userId,
            @Valid @RequestBody CurrencyExchangeRequest request
    ) {
        ExchangeHistoryDto result = currencyExchangeUsecase.exchange(request.toCommand(userId));
        return ResponseEntity.ok(ApiResponse.success(ExchangeHistoryResponse.from(result)));
    }

    @GetMapping("/history")
    @Operation(summary = "환전 내역 조회")
    public ResponseEntity<ApiResponse<PageResponse<ExchangeHistoryResponse>>> getHistory(
            @Positive @RequestHeader("X-MEMBER-ID") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : null;

        Page<ExchangeHistoryDto> histories = exchangeHistoryService
                .getExchangeHistories(userId, fromDateTime, toDateTime, pageable);
        PageResponse<ExchangeHistoryResponse> response = new PageResponse<>(
                histories.map(ExchangeHistoryResponse::from)
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}