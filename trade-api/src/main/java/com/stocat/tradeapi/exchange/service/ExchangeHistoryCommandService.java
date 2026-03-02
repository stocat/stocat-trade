package com.stocat.tradeapi.exchange.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.exchange.ExchangeHistoryEntity;
import com.stocat.common.repository.ExchangeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeHistoryCommandService {

    private final ExchangeHistoryRepository exchangeHistoryRepository;

    @Transactional
    public ExchangeHistoryEntity save(
            Long userId,
            Currency fromCurrency,
            Currency toCurrency,
            BigDecimal fromAmount,
            BigDecimal toAmount,
            BigDecimal exchangeRate
    ) {
        return exchangeHistoryRepository.save(
                ExchangeHistoryEntity.builder()
                        .userId(userId)
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .fromAmount(fromAmount)
                        .toAmount(toAmount)
                        .exchangeRate(exchangeRate)
                        .build()
        );
    }
}