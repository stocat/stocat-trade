package com.stocat.tradeapi.exchange.usecase;

import com.stocat.common.domain.Currency;
import com.stocat.common.exception.ApiException;
import com.stocat.common.redis.dto.ExchangeRateLock;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.exchange.service.ExchangeHistoryService;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.usecase.dto.CurrencyExchangeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CurrencyExchangeUsecase {

    private final CashService cashService;
    private final ExchangeHistoryService exchangeHistoryService;

    /**
     * 환율 고정 키를 검증하고, 잠긴 환율로 환전을 실행한 뒤 내역을 저장합니다.
     *
     * @param command userId + rateLockKey
     */
    @Transactional
    public ExchangeHistoryDto exchange(CurrencyExchangeCommand command) {
        ExchangeRateLock lock = exchangeHistoryService.findAndDeleteLock(command.rateLockKey())
                .orElseThrow(() -> new ApiException(TradeErrorCode.EXCHANGE_RATE_LOCK_EXPIRED));

        if (!lock.userId().equals(command.userId())) {
            throw new ApiException(TradeErrorCode.EXCHANGE_LOCK_UNAUTHORIZED);
        }

        Currency fromCurrency = Currency.valueOf(lock.fromCurrency());
        Currency toCurrency = Currency.valueOf(lock.toCurrency());

        ExchangeCommand exchangeCommand = ExchangeCommand.builder()
                .userId(command.userId())
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .fromAmount(lock.fromAmount())
                .toAmount(lock.toAmount())
                .build();

        cashService.performExchange(exchangeCommand);

        return exchangeHistoryService.save(exchangeCommand, lock.rate());
    }
}