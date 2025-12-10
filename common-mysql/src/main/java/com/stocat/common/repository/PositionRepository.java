package com.stocat.common.repository;

import com.stocat.common.domain.position.PositionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    Optional<PositionEntity> findByIdAndUserId(Long id, Long userId);

    List<PositionEntity> findPositionsByUserId(Long userId);
}
