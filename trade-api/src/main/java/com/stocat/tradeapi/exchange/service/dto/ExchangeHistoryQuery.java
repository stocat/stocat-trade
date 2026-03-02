package com.stocat.tradeapi.exchange.service.dto;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public record ExchangeHistoryQuery(
        Long userId,
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable
) {
}