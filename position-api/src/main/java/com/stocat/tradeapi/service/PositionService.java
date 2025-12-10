package com.stocat.tradeapi.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.tradeapi.service.dto.PositionDto;
import com.stocat.tradeapi.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.service.dto.command.GetUserPositionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionQueryService positionQueryService;

    public PositionDto getPositionById(GetPositionCommand command) {
        Optional<PositionEntity> userPosition = positionQueryService.getUserPosition(command.positionId(),
                command.userId());

        PositionEntity entity = userPosition.orElseThrow(() ->
                // TODO: 에러 처리
        );

        return PositionDto.from(entity);
    }

    public List<PositionDto> getUserPositions(GetUserPositionCommand command) {
        List<PositionEntity> userPositions = positionQueryService.getUserPositions(command.userId());

        if (userPositions == null || userPositions.isEmpty()) {
            return List.of();
        }

        return userPositions.stream()
                .map(PositionDto::from)
                .toList();
    }
}
