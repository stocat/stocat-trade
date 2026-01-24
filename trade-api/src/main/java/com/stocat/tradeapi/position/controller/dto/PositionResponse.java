package com.stocat.tradeapi.position.controller.dto;

import com.stocat.tradeapi.position.service.dto.PositionDto;
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
@Schema(name = "PositionResponse", description = "포지션 조회 응답")
public class PositionResponse {

    @Schema(description = "포지션 ID", example = "1")
    private Long id;

    @Schema(description = "유저 ID", example = "2")
    private Long userId;

    @Schema(description = "자산 ID", example = "3")
    private Long assetId;

    @Schema(description = "보유 수량", example = "0.5")
    private BigDecimal quantity;

    @Schema(description = "평균 진입가", example = "31250.30")
    private BigDecimal avgEntryPrice;

    public static PositionResponse from(PositionDto dto) {
        return PositionResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .assetId(dto.getAssetId())
                .quantity(dto.getQuantity())
                .avgEntryPrice(dto.getAvgEntryPrice())
                .build();
    }
}
