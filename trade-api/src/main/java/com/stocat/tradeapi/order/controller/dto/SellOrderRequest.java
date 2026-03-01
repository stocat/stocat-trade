package com.stocat.tradeapi.order.controller.dto;

import com.stocat.common.domain.order.OrderTif;
import com.stocat.common.domain.order.OrderType;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellOrderRequest(
        @Schema(description = "주문 타입 (LIMIT/MARKET)", example = "LIMIT")
        @NotBlank(message = "주문 타입은 필수 값입니다.")
        String orderType,

        @Schema(description = "주문 유효 기간 (GTC/IOC)", example = "GTC")
        @NotBlank(message = "주문 유효 기간은 필수 값입니다.")
        String orderTif,

        @Schema(description = "종목 심볼(ticker/종목 코드)", example = "NVDA")
        @NotBlank(message = "종목 심볼 값은 필수입니다.")
        String symbol,

        @Schema(description = "매도량", example = "0.5")
        @NotNull(message = "매도량은 필수 값입니다.")
        BigDecimal quantity,

        @Schema(description = "매도 가격(지정가)", example = "200.20")
        BigDecimal price
) {
    @AssertTrue(message = "지정가 주문은 희망 매도 가격이 필수입니다.")
    private boolean isPriceValid() {
        if (orderType.equals("LIMIT")) {
            return price != null && price.compareTo(BigDecimal.ZERO) > 0;
        }

        if (orderType.equals("MARKET")) {
            return price == null;
        }

        return false;
    }

    public SellOrderCommand toCommand(Long userId, LocalDateTime now) {
        OrderType orderType = OrderType.valueOf(this.orderType);
        OrderTif orderTif = OrderTif.valueOf(this.orderTif);

        return SellOrderCommand.builder()
                .userId(userId)
                .assetSymbol(symbol)
                .orderType(orderType)
                .quantity(quantity)
                .price(price)
                .tif(orderTif)
                .requestTime(now)
                .build();
    }
}
