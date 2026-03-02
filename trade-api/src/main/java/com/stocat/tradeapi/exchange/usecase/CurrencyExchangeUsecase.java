package com.stocat.tradeapi.exchange.usecase;

import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.cash.service.CashService;
import com.stocat.tradeapi.exception.TradeErrorCode;
import com.stocat.tradeapi.exchange.service.ExchangeHistoryService;
import com.stocat.tradeapi.exchange.service.dto.ExchangeCommand;
import com.stocat.tradeapi.exchange.service.dto.ExchangeHistoryDto;
import com.stocat.tradeapi.exchange.usecase.dto.CurrencyExchangeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class CurrencyExchangeUsecase {

    private final CashService cashService;
    private final ExchangeHistoryService exchangeHistoryService;

    @Transactional
    public ExchangeHistoryDto exchange(CurrencyExchangeCommand command) {
        if (command.fromCurrency() == command.toCurrency()) {
            throw new ApiException(TradeErrorCode.SAME_CURRENCY_EXCHANGE);
        }

        BigDecimal rate = exchangeHistoryService.findRate(command.fromCurrency(), command.toCurrency())
                .orElseThrow(() -> new ApiException(TradeErrorCode.EXCHANGE_RATE_NOT_FOUND));

        BigDecimal toAmount = command.fromAmount().multiply(rate).setScale(8, RoundingMode.HALF_UP);

        ExchangeCommand exchangeCommand = new ExchangeCommand(
                command.userId(), command.fromCurrency(), command.toCurrency(), command.fromAmount(), toAmount);

        cashService.performExchange(exchangeCommand);

        return exchangeHistoryService.save(exchangeCommand, rate);
    }
}