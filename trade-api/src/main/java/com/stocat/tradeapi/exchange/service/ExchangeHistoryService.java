package com.stocat.tradeapi.exchange.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.redis.repository.ExchangeRateRedisRepository;
import com.stocat.tradeapi.common.dto.PageResponse;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeHistoryService {

    private final ExchangeRateRedisRepository exchangeRateRedisRepository;
    private final ExchangeHistoryCommandService exchangeHistoryCommandService;
    private final ExchangeHistoryQueryService exchangeHistoryQueryService;

    public Optional<BigDecimal> findRate(Currency fromCurrency, Currency toCurrency) {
        String pair = fromCurrency.name() + toCurrency.name();
        return exchangeRateRedisRepository.findRate(pair);
    }

    @Transactional
    public ExchangeHistoryDto save(ExchangeCommand command, BigDecimal rate) {
        return ExchangeHistoryDto.from(
                exchangeHistoryCommandService.save(
                        command.userId(), command.fromCurrency(), command.toCurrency(),
                        command.fromAmount(), command.toAmount(), rate)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ExchangeHistoryDto> getExchangeHistories(
            Long userId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return new PageResponse<>(exchangeHistoryQueryService.getExchangeHistories(userId, from, to, pageable));
    }
}