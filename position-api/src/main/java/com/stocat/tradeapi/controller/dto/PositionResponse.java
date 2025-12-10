package com.stocat.tradeapi.controller.dto;

import com.stocat.common.domain.position.PositionDirection;
import com.stocat.common.domain.position.PositionStatus;
import com.stocat.tradeapi.service.dto.PositionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Schema(description = "포지션 방향", example = "LONG", allowableValues = {"LONG", "SHORT"})
    private PositionDirection direction;

    @Schema(description = "포지션 상태", example = "OPEN", allowableValues = {"OPEN", "CLOSED", "EXPIRED"})
    private PositionStatus status;

    @Schema(description = "보유 수량", example = "0.5")
    private BigDecimal quantity;

    @Schema(description = "평균 진입가", example = "31250.30")
    private BigDecimal avgEntryPrice;

    @Schema(description = "포지션 오픈 시간", example = "2024-01-01T10:00:00")
    private LocalDateTime openedAt;

    @Schema(description = "포지션 종료 시간", example = "2024-01-02T15:30:00")
    private LocalDateTime closedAt;

    @Schema(description = "포지션 만료 시간", example = "2024-01-03T00:00:00")
    private LocalDateTime expiresAt;

    public static PositionResponse from(PositionDto dto) {
        return PositionResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .assetId(dto.getAssetId())
                .direction(dto.getDirection())
                .status(dto.getStatus())
                .quantity(dto.getQuantity())
                .avgEntryPrice(dto.getAvgEntryPrice())
                .openedAt(dto.getOpenedAt())
                .closedAt(dto.getClosedAt())
                .expiresAt(dto.getExpiresAt())
                .build();
    }
}
