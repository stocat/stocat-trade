package com.stocat.tradeapi.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.repository.PositionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PositionQueryService {

    private final PositionRepository positionRepository;

    public Optional<PositionEntity> getUserPosition(Long id, Long userId){
        return positionRepository.findByIdAndUserId(id,userId);
    }

    public List<PositionEntity> getUserPositions(Long userId) {
        return positionRepository.findPositionsByUserId(userId);
    }
}
