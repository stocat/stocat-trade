package com.stocat.tradeapi.fill.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FillBuyOrderRequest(
        @Schema(description = "체결 대상 주문 ID", example = "10001")
        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @Schema(description = "체결된 자산 ID", example = "31")
        @NotNull(message = "자산 ID는 필수입니다.")
        Long assetId,

        @Schema(description = "거래 구분 (BUY)", example = "BUY")
        @NotBlank(message = "거래 구분은 필수입니다.")
        String side,

        @Schema(description = "체결 수량", example = "1.25")
        @NotNull(message = "체결 수량은 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "체결 수량은 0보다 커야 합니다.")
        BigDecimal quantity,

        @Schema(description = "체결 단가", example = "125.25")
        @NotNull(message = "체결 단가는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "체결 단가는 0보다 커야 합니다.")
        BigDecimal price,

        @Schema(description = "체결 통화", example = "USD")
        @NotBlank(message = "체결 통화는 필수입니다.")
        String priceCurrency,

        @Schema(description = "수수료 금액", example = "0.35")
        @DecimalMin(value = "0.0", message = "수수료는 0 이상이어야 합니다.")
        BigDecimal feeAmount,

        @Schema(description = "수수료 통화", example = "USD")
        String feeCurrency,

        @Schema(description = "체결 일시", example = "2024-09-01T12:30:00")
        @NotNull(message = "체결 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
        LocalDateTime executedAt,

        @Schema(description = "환율 ID", example = "445")
        Long exchangeRateId,

        @Schema(description = "적용 환율", example = "1312.50")
        @DecimalMin(value = "0.0", inclusive = false, message = "환율은 0보다 커야 합니다.")
        BigDecimal exchangeRate,

        @Schema(description = "환율 페어", example = "USD/KRW")
        String pair
) {
    public FillBuyOrderCommand toCommand() {
        TradeSide tradeSide = TradeSide.valueOf(side.toUpperCase());
        Currency tradeCurrency = Currency.valueOf(priceCurrency.toUpperCase());
        Currency feeCurrencyEnum = feeCurrency == null ? null : Currency.valueOf(feeCurrency.toUpperCase());

        return new FillBuyOrderCommand(
                orderId,
                assetId,
                tradeSide,
                quantity,
                price,
                tradeCurrency,
                feeAmount,
                feeCurrencyEnum,
                executedAt,
                exchangeRateId,
                exchangeRate,
                pair
        );
    }

    @AssertTrue(message = "거래 방향은 BUY 만 허용됩니다.")
    private boolean isBuySide() {
        return side != null && TradeSide.BUY.name().equalsIgnoreCase(side);
    }

    @AssertTrue(message = "수수료 금액과 통화는 함께 제공되어야 합니다.")
    private boolean isFeeInfoConsistent() {
        if (feeAmount == null) {
            return feeCurrency == null;
        }
        return feeCurrency != null && !feeCurrency.isBlank();
    }
}
