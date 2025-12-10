package com.stocat.tradeapi.service.dto;

import com.stocat.common.domain.position.PositionDirection;
import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.domain.position.PositionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionDto {

    private final Long id;
    private final Long userId;
    private final Long assetId;
    private final PositionStatus status;
    private final PositionDirection direction;
    private final BigDecimal quantity;
    private final BigDecimal avgEntryPrice;
    private final LocalDateTime expiresAt;
    private final LocalDateTime openedAt;
    private final LocalDateTime closedAt;

    public static PositionDto from(PositionEntity entity) {
        return PositionDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .assetId(entity.getAssetId())
                .status(entity.getStatus())
                .direction(entity.getDirection())
                .quantity(entity.getQuantity())
                .avgEntryPrice(entity.getAvgEntryPrice())
                .expiresAt(entity.getExpiresAt())
                .openedAt(entity.getOpenedAt())
                .closedAt(entity.getClosedAt())
                .build();
    }
}
