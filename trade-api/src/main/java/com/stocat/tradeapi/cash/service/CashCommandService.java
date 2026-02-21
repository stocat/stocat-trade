package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CashCommandService {

    private final CashHoldingRepository cashHoldingRepository;

    public CashHoldingEntity createCashHolding(CashBalanceEntity balance, BigDecimal amount) {
        try {
            balance.reserve(amount);
        } catch (IllegalStateException ex) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }
        return cashHoldingRepository.save(CashHoldingEntity.hold(balance.getId(), amount));
    }

    public void consumeHolding(CashHoldingEntity holding, CashBalanceEntity balance) {
        try {
            holding.consume();
            balance.settleReservedAmount(holding.getAmount());
        } catch (IllegalStateException ex) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }
    }
}
