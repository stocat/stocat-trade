package com.stocat.common.repository;

import com.stocat.common.domain.exchange.ExchangeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistoryEntity, Long>,
        ExchangeHistoryCustomRepository {
}