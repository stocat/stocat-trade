package com.stocat.common.domain.asset.repository;

import com.stocat.common.domain.asset.domain.AssetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetsRepository extends JpaRepository<AssetsEntity, Long> {
}
