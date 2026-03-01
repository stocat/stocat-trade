package com.stocat.tradeapi.order.controller;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.order.controller.dto.BuyOrderRequest;
import com.stocat.tradeapi.order.controller.dto.OrderResponse;
import com.stocat.tradeapi.order.controller.dto.SellOrderRequest;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import com.stocat.tradeapi.order.service.dto.command.SellOrderCommand;
import com.stocat.tradeapi.order.usecase.BuyOrderUsecase;
import com.stocat.tradeapi.order.usecase.CancelOrderUsecase;
import com.stocat.tradeapi.order.usecase.SellOrderUsecase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final BuyOrderUsecase buyOrderUsecase;
    private final SellOrderUsecase sellOrderUsecase;
    private final CancelOrderUsecase cancelOrderUsecase;

    @PostMapping("/buy")
    @Operation(summary = "매수 주문 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "매수 주문 생성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "현금 부족, 장 마감 등")
    public ResponseEntity<ApiResponse<OrderResponse>> placeBuyOrder(
            @RequestHeader("X-MEMBER-ID") Long userId,
            @Valid @RequestBody BuyOrderRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();
        BuyOrderCommand command = request.toCommand(userId, now);

        OrderResponse response = OrderResponse.from(buyOrderUsecase.placeBuyOrder(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sell")
    @Operation(summary = "매도 주문 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "매도 주문 생성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "포지션 부족 등")
    public ResponseEntity<ApiResponse<OrderResponse>> placeSellOrder(
            @RequestHeader("X-MEMBER-ID") Long userId,
            @Valid @RequestBody SellOrderRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();
        SellOrderCommand command = request.toCommand(userId, now);

        OrderResponse response = OrderResponse.from(sellOrderUsecase.placeSellOrder(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "매수/매도 주문 취소")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 취소 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소할 수 없는 거래 (이미 체결됨 등)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestHeader("X-MEMBER-ID") Long userId,
            @PathVariable @Positive Long orderId

    ) {
        OrderCancelCommand command = new OrderCancelCommand(orderId, userId);

        OrderDto order = cancelOrderUsecase.cancelOrder(command);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
