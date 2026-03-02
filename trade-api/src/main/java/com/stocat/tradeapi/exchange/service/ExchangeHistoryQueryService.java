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

    /** 기간 필터와 페이지네이션을 적용하여 사용자의 환전 내역을 조회합니다. */
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