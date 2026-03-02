package com.stocat.tradeapi.exchange.service;

import com.stocat.common.domain.exchange.ExchangeHistoryEntity;
import com.stocat.common.repository.ExchangeHistoryRepository;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeHistoryCommandService {

    private final ExchangeHistoryRepository exchangeHistoryRepository;

    /** 환전 내역 엔티티를 생성하여 저장합니다. */
    @Transactional
    public ExchangeHistoryEntity save(ExchangeCommand command, BigDecimal exchangeRate) {
        return exchangeHistoryRepository.save(
                ExchangeHistoryEntity.builder()
                        .userId(command.userId())
                        .fromCurrency(command.fromCurrency())
                        .toCurrency(command.toCurrency())
                        .fromAmount(command.fromAmount())
                        .toAmount(command.toAmount())
                        .exchangeRate(exchangeRate)
                        .build()
        );
    }
}