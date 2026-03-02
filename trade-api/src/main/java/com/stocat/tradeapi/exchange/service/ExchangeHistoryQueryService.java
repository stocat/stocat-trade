package com.stocat.tradeapi.exchange.service;

import com.stocat.common.repository.ExchangeHistoryRepository;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeHistoryQueryService {

    private final ExchangeHistoryRepository exchangeHistoryRepository;

    public Page<ExchangeHistoryDto> getExchangeHistories(
            Long userId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return exchangeHistoryRepository
                .findExchanges(userId, from, to, pageable)
                .map(ExchangeHistoryDto::from);
    }
}