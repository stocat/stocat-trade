package com.stocat.common.repository;

import com.stocat.common.domain.position.PositionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    List<PositionEntity> findPositionsByUserId(Long userId);

    Optional<PositionEntity> findByAssetIdAndUserId(Long assetId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PositionEntity p WHERE p.assetId = :assetId AND p.userId = :userId")
    Optional<PositionEntity> findByAssetIdAndUserIdForUpdate(@Param("assetId") Long assetId, @Param("userId") Long userId);
}
