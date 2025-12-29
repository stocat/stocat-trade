package com.stocat.tradeapi.position.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "NewPositionRequest", description = "새 포지션 생성 요청")
public class PositionUpsertRequest {

    @Schema(description = "유저 ID", example = "2")
    private Long userId;

    @Schema(description = "자산 ID", example = "3")
    private Long assetId;

    @Schema(description = "추가 수량", example = "0.5")
    private BigDecimal quantity;

    @Schema(description = "추가 진입가", example = "31250.30")
    private BigDecimal avgEntryPrice;
}
