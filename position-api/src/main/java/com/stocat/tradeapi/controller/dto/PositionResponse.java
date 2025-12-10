package com.stocat.tradeapi.controller.dto;

import com.stocat.common.domain.position.PositionDirection;
import com.stocat.common.domain.position.PositionStatus;
import com.stocat.tradeapi.service.dto.PositionDto;
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
public class PositionResponse {

    private Long id;
    private Long userId;
    private Long assetId;
    private PositionDirection direction;
    private PositionStatus status;
    private BigDecimal quantity;
    private BigDecimal avgEntryPrice;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
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
