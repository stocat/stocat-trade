package com.stocat.tradeapi.order.controller.dto;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.TradeSide;
import com.stocat.common.domain.order.OrderStatus;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        @Schema(description = "주문 고유 ID", example = "1001")
        Long id,
        @Schema(description = "종목 고유 ID", example = "1")
        Long assetId,
        @Schema(description = "매수/매도 구분", example = "BUY | SELL")
        TradeSide side,
        @Schema(description = "주문 상태", example = "PENDING | FILLED | CANCELED | REJECTED")
        String status,
        @Schema(description = "거래 수량", example = "PENDING | FILLED | CANCELED | REJECTED")
        BigDecimal quantity,
        @Schema(description = "지정가 (시장가 요청시 null)", example = "25000.0")
        BigDecimal price,
        @Schema(description = "주문 생성 일시", example = "2025-12-07T15:30:45.123")
        LocalDateTime createdAt
) {
    public static OrderResponse from(OrderDto order) {
        return new OrderResponse(
                order.id(),
                order.assetId(),
                order.side(),
                order.status().name(),
                order.quantity(),
                order.price(),
                order.createdAt()
        );
    }
}
