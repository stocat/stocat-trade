package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.PositionRepository;
import com.stocat.tradeapi.position.exception.PositionErrorCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionQueryService {

    private final PositionRepository positionRepository;

    @Transactional(readOnly = true)
    public PositionEntity getPositionById(Long id) {
        return positionRepository
                .findById(id)
                .orElseThrow(() -> new ApiException(PositionErrorCode.NOT_FOUND_USER_POSITION));
    }

    @Transactional(readOnly = true)
    public List<PositionEntity> getUserPositions(Long userId) {
        return positionRepository.findPositionsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<PositionEntity> getUserPosition(Long assetId, Long userId) {
        return positionRepository.findByAssetIdAndUserId(assetId, userId);
    }

    @Transactional
    public PositionEntity saveUserPosition(PositionEntity entity) {
        return positionRepository.save(entity);
    }
}
