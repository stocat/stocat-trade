package com.stocat.tradeapi.exchange.controller;

import com.stocat.common.exception.ApiException;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.common.dto.PageResponse;
import com.stocat.tradeapi.exchange.controller.dto.CurrencyExchangeRequest;
import com.stocat.tradeapi.exchange.controller.dto.ExchangeHistoryResponse;
import com.stocat.tradeapi.exchange.controller.dto.ExchangeHistorySearchRequest;
import com.stocat.tradeapi.exchange.controller.dto.ExchangePreviewRequest;
import com.stocat.tradeapi.exchange.controller.dto.ExchangePreviewResponse;
import com.stocat.tradeapi.exchange.service.ExchangeHistoryService;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryQuery;
import com.stocat.tradeapi.exchange.usecase.CurrencyExchangeUsecase;
import com.stocat.tradeapi.exception.TradeErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/exchange")
@Tag(name = "Exchange", description = "환전 API")
@RequiredArgsConstructor
public class ExchangeController {

    private final CurrencyExchangeUsecase currencyExchangeUsecase;
    private final ExchangeHistoryService exchangeHistoryService;

    @GetMapping("/preview")
    @Operation(summary = "환전 금액 미리보기", description = "잔고 변경 없이 환전 예상 금액과 적용 환율을 반환합니다. fromAmount 또는 toAmount 중 하나만 입력하세요.")
    public ResponseEntity<ApiResponse<ExchangePreviewResponse>> preview(
            @Valid @ModelAttribute ExchangePreviewRequest request
    ) {
        validateExclusiveAmountParam(request.fromAmount(), request.toAmount());
        return ResponseEntity.ok(ApiResponse.success(
                ExchangePreviewResponse.from(exchangeHistoryService.preview(request.toQuery()))));
    }

    /**
     * fromAmount와 toAmount 중 정확히 하나만 입력되었는지 검증합니다.
     * 둘 다 null이거나 둘 다 입력된 경우 예외를 던집니다.
     */
    private void validateExclusiveAmountParam(BigDecimal fromAmount, BigDecimal toAmount) {
        if ((fromAmount == null) == (toAmount == null)) {
            throw new ApiException(TradeErrorCode.EXCHANGE_PREVIEW_PARAM_INVALID);
        }
    }

    @PostMapping
    @Operation(summary = "환전 실행")
    public ResponseEntity<ApiResponse<ExchangeHistoryResponse>> executeExchange(
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
            @ModelAttribute ExchangeHistorySearchRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        ExchangeHistoryQuery query = request.toQuery(userId, pageable);
        Page<ExchangeHistoryDto> histories = exchangeHistoryService.getExchangeHistories(query);
        return ResponseEntity.ok(ApiResponse.success(new PageResponse<>(histories.map(ExchangeHistoryResponse::from))));
    }
}