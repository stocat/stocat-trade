package com.stocat.tradeapi.order.controller.dto;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuyOrderRequest(
        @Schema(description = "주문 타입 (LIMIT/MARKET)", example = "LIMIT")
        @NotBlank(message = "주문 타입은 필수 값입니다.")
        String orderType,

        @Schema(description = "주문 유효 기간 (GTC/IOC)", example = "GTC")
        @NotBlank(message = "주문 유효 기간은 필수 값입니다.")
        String orderTif,

        @Schema(description = "종목 심볼(ticker/종목 코드)", example = "NVDA")
        @NotBlank(message = "종목 심볼 값은 필수입니다.")
        String symbol,

        @Schema(description = "매수량", example = "0.5")
        @NotNull(message = "매수량은 필수 값입니다.")
        BigDecimal quantity,

        @Schema(description = "지정가", example = "200.20")
        BigDecimal price
) {
    @AssertTrue(message = "지정가 주문은 0원 이상의 희망 매수 가격이 필수입니다.")
    private boolean isPriceValid() {
        if ("LIMIT".equals(orderType)) {
            return price != null && price.compareTo(BigDecimal.ZERO) > 0;
        }

        if ("MARKET".equals(orderType)) {
            return price == null;
        }

        return false;
    }

    public BuyOrderCommand toCommand(Long userId, LocalDateTime now) {
        OrderType orderType = OrderType.valueOf(this.orderType);
        OrderTif orderTif = OrderTif.valueOf(this.orderTif);

        return BuyOrderCommand.builder()
                .userId(userId)
                .orderType(orderType)
                .assetSymbol(symbol)
                .quantity(quantity)
                .price(price)
                .tif(orderTif)
                .requestTime(now)
                .build();
    }
}
