package com.stocat.common.repository;

import com.stocat.common.domain.position.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    List<PositionEntity> findPositionsByUserId(Long userId);

    Optional<PositionEntity> findFirstByAssetIdAndUserId(Long assetId, Long userId);
}
