package com.stocat.tradeapi.position.service.dto;

import com.stocat.common.domain.position.PositionEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionDto {

    private final Long id;
    private final Long userId;
    private final Long assetId;
    private final BigDecimal quantity;
    private final BigDecimal avgEntryPrice;

    public static PositionDto from(PositionEntity entity) {
        return PositionDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .assetId(entity.getAssetId())
                .quantity(entity.getQuantity())
                .avgEntryPrice(entity.getAvgEntryPrice())
                .build();
    }
}
