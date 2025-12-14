package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.position.exception.PositionErrorCode;
import com.stocat.tradeapi.position.service.dto.PositionDto;
import com.stocat.tradeapi.position.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.position.service.dto.command.GetUserPositionCommand;
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
