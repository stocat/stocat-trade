package com.stocat.tradeapi.exchange.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.exception.ApiException;
import com.stocat.common.redis.constants.ExchangeRateLockKeys;
import com.stocat.common.redis.dto.ExchangeRateLock;
import com.stocat.common.redis.repository.ExchangeRateLockRepository;
import com.stocat.common.redis.repository.ExchangeRateRedisRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryQuery;
import com.stocat.tradeapi.exchange.service.dto.ExchangePreviewDto;
import com.stocat.tradeapi.exchange.service.dto.ExchangePreviewQuery;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeHistoryService {

    private final ExchangeRateRedisRepository exchangeRateRedisRepository;
    private final ExchangeRateLockRepository exchangeRateLockRepository;
    private final ExchangeHistoryCommandService exchangeHistoryCommandService;
    private final ExchangeHistoryQueryService exchangeHistoryQueryService;

    /** Redis에서 통화 쌍에 해당하는 현재 환율을 조회합니다. */
    public Optional<BigDecimal> findRate(Currency fromCurrency, Currency toCurrency) {
        String pair = fromCurrency.name() + toCurrency.name();
        return exchangeRateRedisRepository.findRate(pair);
    }

    /**
     * 환전 예상 금액을 계산하고, 적용 환율을 30초간 Redis에 고정하여 잠금 키를 반환합니다.
     * fromAmount 또는 toAmount 중 하나만 전달하면 나머지를 역산합니다.
     */
    public ExchangePreviewDto preview(ExchangePreviewQuery query) {
        BigDecimal rate = findRate(query.fromCurrency(), query.toCurrency())
                .orElseThrow(() -> new ApiException(TradeErrorCode.EXCHANGE_RATE_NOT_FOUND));

        BigDecimal resolvedFromAmount;
        BigDecimal resolvedToAmount;
        if (query.fromAmount() != null) {
            resolvedFromAmount = query.fromAmount();
            resolvedToAmount = query.fromAmount().multiply(rate).setScale(8, RoundingMode.HALF_UP);
        } else {
            resolvedToAmount = query.toAmount();
            resolvedFromAmount = query.toAmount().divide(rate, 8, RoundingMode.HALF_UP);
        }

        ExchangeRateLock lock = new ExchangeRateLock(
                query.fromCurrency().name(), query.toCurrency().name(), resolvedFromAmount, resolvedToAmount, rate);
        String rateLockKey = exchangeRateLockRepository.store(lock);

        return new ExchangePreviewDto(resolvedFromAmount, resolvedToAmount, rate, rateLockKey, ExchangeRateLockKeys.LOCK_TTL.getSeconds());
    }

    /** 환율 잠금 키에 해당하는 데이터를 조회하고 즉시 삭제합니다 (단일 사용 보장). */
    public Optional<ExchangeRateLock> findAndDeleteLock(String rateLockKey) {
        return exchangeRateLockRepository.getAndDelete(rateLockKey);
    }

    /** 환전 내역을 저장하고 DTO로 반환합니다. */
    @Transactional
    public ExchangeHistoryDto save(ExchangeCommand command, BigDecimal rate) {
        return ExchangeHistoryDto.from(exchangeHistoryCommandService.save(command, rate));
    }

    /** 사용자의 환전 내역을 기간 필터와 페이지네이션을 적용하여 조회합니다. */
    @Transactional(readOnly = true)
    public Page<ExchangeHistoryDto> getExchangeHistories(ExchangeHistoryQuery query) {
        return exchangeHistoryQueryService.getExchangeHistories(query);
    }
}