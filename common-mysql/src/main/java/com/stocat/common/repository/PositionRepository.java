package com.stocat.common.repository;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.domain.position.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    Optional<PositionEntity> findByIdAndUserId(Long id, Long userId);

    List<PositionEntity> findPositionsByUserId(Long userId);

    Optional<PositionEntity> findFirstByStatus(PositionStatus status);
}
