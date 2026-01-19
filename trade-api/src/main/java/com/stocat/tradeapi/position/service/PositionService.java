package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.position.exception.PositionErrorCode;
import com.stocat.tradeapi.position.service.dto.PositionDto;
import com.stocat.tradeapi.position.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.position.service.dto.command.PositionUpsertCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionQueryService positionQueryService;

    public PositionDto getPositionById(GetPositionCommand command) {
        PositionEntity userPosition =
                positionQueryService.getPositionById(command.positionId());

        if (!Objects.equals(userPosition.getUserId(), command.userId())) {
            throw new ApiException(PositionErrorCode.NOT_USER_POSITION);
        }

        return PositionDto.from(userPosition);
    }

    public List<PositionDto> getUserPositions(Long userId) {
        List<PositionEntity> userPositions = positionQueryService.getUserPositions(userId);

        if (userPositions == null) {
            return List.of();
        }

        return userPositions.stream()
                .map(PositionDto::from)
                .toList();
    }

    public void updateUserPosition(PositionUpsertCommand command) {
        Optional<PositionEntity> entity = positionQueryService.getUserPosition(command.assetId(), command.userId());

        if (entity.isEmpty()) {
            createNewPosition(command);
            return;
        }

        PositionEntity existingPosition = entity.get();
        applyQuantityChange(existingPosition, command.quantity(), command.avgEntryPrice());
        positionQueryService.saveUserPosition(existingPosition);
    }

    private void createNewPosition(PositionUpsertCommand command) {
        if (command.quantity() == null || command.quantity().signum() <= 0) {
            throw new ApiException(PositionErrorCode.POSITION_NOT_FOUND_FOR_SELL);
        }
        PositionEntity newEntity = PositionEntity.create(
                command.userId(),
                command.assetId(),
                command.quantity(),
                command.avgEntryPrice()
        );
        positionQueryService.saveUserPosition(newEntity);
    }

    private void applyQuantityChange(PositionEntity entity,
                                     BigDecimal quantityDelta,
                                     BigDecimal additionalAvgEntryPrice) {
        if (quantityDelta.signum() == 0) {
            throw new ApiException(PositionErrorCode.INVALID_POSITION_QUANTITY);
        }


        if (quantityDelta.signum() > 0) {
            entity.add(quantityDelta, additionalAvgEntryPrice);
            return;
        }

        entity.subtract(quantityDelta.abs());
    }
}
