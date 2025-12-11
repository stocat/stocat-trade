package com.stocat.tradeapi.order.controller;

import com.stocat.common.domain.order.OrderType;
import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.order.controller.dto.BuyOrderRequest;
import com.stocat.tradeapi.order.controller.dto.OrderResponse;
import com.stocat.tradeapi.infrastructure.dto.AssetDto;
import com.stocat.tradeapi.order.service.OrderService;
import com.stocat.tradeapi.order.service.dto.OrderDto;
import com.stocat.tradeapi.order.service.dto.command.BuyOrderCommand;
import com.stocat.tradeapi.order.service.dto.command.OrderCancelCommand;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/buy")
    @Operation(summary = "매수 주문 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "매수 주문 생성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "현금 부족, 장 마감 등")
    public ResponseEntity<ApiResponse<OrderResponse>> placeBuyOrder(
            @RequestParam Long memberId,
            @Valid @RequestBody BuyOrderRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();
        BuyOrderCommand command = request.toCommand(memberId, now);

        OrderResponse response = OrderResponse.from(orderService.placeBuyOrder(command));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "매수/매도 주문 취소")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 취소 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소할 수 없는 거래 (이미 체결됨 등)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable @Positive Long orderId,
            @RequestParam @Positive Long memberId
    ) {
        OrderCancelCommand command = new OrderCancelCommand(orderId, memberId);

        OrderDto order = orderService.cancelOrder(command);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
