package com.stocat.common.repository;

import com.stocat.common.domain.fill.TradeFillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeFillRepository extends JpaRepository<TradeFillEntity, Long> {
}
