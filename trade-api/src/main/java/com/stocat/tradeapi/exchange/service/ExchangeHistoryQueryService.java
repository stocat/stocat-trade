package com.stocat.tradeapi.exchange.service;

import com.stocat.common.repository.ExchangeHistoryRepository;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeHistoryQueryService {

    private final ExchangeHistoryRepository exchangeHistoryRepository;

    /** 기간 필터와 페이지네이션을 적용하여 사용자의 환전 내역을 조회합니다. */
    public Page<ExchangeHistoryDto> getExchangeHistories(ExchangeHistoryQuery query) {
        return exchangeHistoryRepository
                .findExchanges(query.userId(), query.from(), query.to(), query.pageable())
                .map(ExchangeHistoryDto::from);
    }
}