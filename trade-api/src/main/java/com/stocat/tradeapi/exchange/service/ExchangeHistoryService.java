package com.stocat.tradeapi.exchange.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.exception.ApiException;
import com.stocat.common.redis.repository.ExchangeRateRedisRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.service.dto.ExchangePreviewDto;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public ExchangePreviewDto preview(Currency fromCurrency, Currency toCurrency, BigDecimal fromAmount) {
        BigDecimal rate = findRate(fromCurrency, toCurrency)
                .orElseThrow(() -> new ApiException(TradeErrorCode.EXCHANGE_RATE_NOT_FOUND));
        BigDecimal toAmount = fromAmount.multiply(rate).setScale(8, RoundingMode.HALF_UP);
        return new ExchangePreviewDto(toAmount, rate);
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
    public Page<ExchangeHistoryDto> getExchangeHistories(
            Long userId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return exchangeHistoryQueryService.getExchangeHistories(userId, from, to, pageable);
    }
}