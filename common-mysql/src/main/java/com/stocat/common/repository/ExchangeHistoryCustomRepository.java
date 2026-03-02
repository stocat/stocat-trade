package com.stocat.common.repository;

import com.stocat.common.domain.exchange.ExchangeHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ExchangeHistoryCustomRepository {

    Page<ExchangeHistoryEntity> findExchanges(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}