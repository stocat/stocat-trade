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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionQueryService positionQueryService;

    public PositionDto getPositionById(GetPositionCommand command) {
        PositionEntity userPosition =
                positionQueryService.getPositionById(command.positionId());

        // TODO: 유저의 아이디 일치 여부 검증

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

        try {
            PositionEntity newEntity = PositionEntity.create(
                    command.userId(),
                    command.assetId(),
                    command.quantity(),
                    command.avgEntryPrice()
            );
            positionQueryService.saveUserPosition(newEntity);
        } catch (IllegalArgumentException e) {
            throw new ApiException(PositionErrorCode.INVALID_POSITION_QUANTITY, e);
        }
    }

    private void applyQuantityChange(PositionEntity entity,
                                     BigDecimal quantityDelta,
                                     BigDecimal additionalAvgEntryPrice) {
        try {
            if (quantityDelta.signum() > 0) {
                entity.add(quantityDelta, additionalAvgEntryPrice);
                return;
            }

            if (quantityDelta.signum() < 0) {
                entity.subtract(quantityDelta.abs());
                return;
            }

            throw new ApiException(PositionErrorCode.INVALID_POSITION_QUANTITY);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ApiException(PositionErrorCode.INVALID_POSITION_QUANTITY, e);
        }
    }
}
