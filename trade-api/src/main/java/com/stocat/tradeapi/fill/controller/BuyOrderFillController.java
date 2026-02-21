package com.stocat.tradeapi.fill.controller;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.fill.controller.dto.FillBuyOrderRequest;
import com.stocat.tradeapi.fill.dto.FillBuyOrderCommand;
import com.stocat.tradeapi.fill.usecase.BuyOrderExecutionUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "Trade Fill", description = "체결 엔드포인트")
@RestController
@RequestMapping("/fills")
@RequiredArgsConstructor
public class BuyOrderFillController {

    private final BuyOrderExecutionUsecase buyOrderExecutionUsecase;

    @Operation(summary = "매수 주문 체결 처리", description = "체결 정보를 전달받아 주문, 체결, 포지션, 현금을 업데이트합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "체결 처리 성공")
    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Void>> fillBuyOrder(
            @Valid @RequestBody FillBuyOrderRequest request
    ) {
        FillBuyOrderCommand command = request.toCommand();
        buyOrderExecutionUsecase.fillBuyOrder(command);

        return ResponseEntity.ok(ApiResponse.success());
    }
}
