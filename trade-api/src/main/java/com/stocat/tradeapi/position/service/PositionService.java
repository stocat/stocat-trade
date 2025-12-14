package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.position.exception.PositionErrorCode;
import com.stocat.tradeapi.position.service.dto.PositionDto;
import com.stocat.tradeapi.position.service.dto.command.GetPositionCommand;
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
}
