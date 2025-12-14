package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.domain.position.PositionStatus;
import com.stocat.tradeapi.position.service.dto.PositionDto;
import com.stocat.tradeapi.position.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.position.service.dto.command.NewPositionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionQueryService positionQueryService;

    public PositionDto getPositionById(GetPositionCommand command) {
        PositionEntity userPosition =
                positionQueryService.getUserPosition(command.positionId(), command.userId());

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

    public void createNewUserPosition(NewPositionCommand command) {
        Optional<PositionEntity> entity = positionQueryService.getUserPositionByStatus(PositionStatus.OPEN);

        if (entity.isEmpty()) {
            // OPEN 상태인 포지션이 없다면 신규 포지션 생성
            PositionEntity newEntity = PositionEntity.create(
                    command.userId(),
                    command.assetId(),
                    PositionStatus.OPEN,
                    command.quantity(),
                    command.avgEntryPrice(),
                    command.openedAt()
            );
            positionQueryService.createNewUserPosition(newEntity);
            return;
        }

        // TODO: OPEN 상태인 포지션이 있으면 수량 및 평균단가 갱신
    }
}
