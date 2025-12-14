package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.domain.position.PositionStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.PositionRepository;
import com.stocat.tradeapi.position.exception.PositionErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PositionQueryService {

    private final PositionRepository positionRepository;

    public PositionEntity getUserPosition(Long id, Long userId) {
        return positionRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(PositionErrorCode.NOT_FOUND_USER_POSITION));
    }

    public List<PositionEntity> getUserPositions(Long userId) {
        return positionRepository.findPositionsByUserId(userId);
    }

    public Optional<PositionEntity> getUserPositionByStatus(PositionStatus status) {
        return positionRepository.findFirstByStatus(status);
    }

    public PositionEntity createNewUserPosition(PositionEntity entity) {
        return positionRepository.save(entity);
    }
}
