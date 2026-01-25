package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.tradeapi.cash.exception.CashErrorCode;
import com.stocat.tradeapi.cash.service.dto.command.CreateCashHoldingCommand;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CashHoldingRepository cashHoldingRepository;

    @Transactional
    public void createCashHolding(CreateCashHoldingCommand command) {
        validateAmount(command.amount());

        CashBalanceEntity balance = cashBalanceRepository
                .findByUserIdAndCurrencyForUpdate(command.userId(), command.currency())
                .orElseThrow(() -> new ApiException(CashErrorCode.CASH_BALANCE_NOT_FOUND));

        BigDecimal available = balance.getBalance().subtract(getActiveHeldAmount(balance.getId()));
        if (available.compareTo(command.amount()) < 0) {
            throw new ApiException(CashErrorCode.INSUFFICIENT_CASH_BALANCE);
        }

        CashHoldingEntity holding = CashHoldingEntity.hold(balance.getId(), command.orderId(), command.amount());
        cashHoldingRepository.save(holding);
    }

    @Transactional
    public void consumeHoldingAndWithdraw(Long orderId) {
        CashHoldingEntity holding = cashHoldingRepository
                .findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ApiException(CashErrorCode.CASH_HOLDING_NOT_FOUND));

        try {
            holding.consume();
        } catch (IllegalStateException ex) {
            throw new ApiException(CashErrorCode.CASH_HOLDING_ALREADY_FINALIZED);
        }

        CashBalanceEntity balance = cashBalanceRepository
                .findByIdForUpdate(holding.getCashBalanceId())
                .orElseThrow(() -> new ApiException(CashErrorCode.CASH_BALANCE_NOT_FOUND));

        if (balance.getBalance().compareTo(holding.getAmount()) < 0) {
            throw new ApiException(CashErrorCode.INSUFFICIENT_CASH_BALANCE);
        }

        balance.withdraw(holding.getAmount());
        cashBalanceRepository.save(balance);
        cashHoldingRepository.save(holding);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(CashErrorCode.INVALID_CASH_AMOUNT);
        }
    }

    private BigDecimal getActiveHeldAmount(Long cashBalanceId) {
        BigDecimal heldAmount = cashHoldingRepository
                .sumAmountByCashBalanceIdAndStatus(cashBalanceId, CashHoldingStatus.HELD);

        return heldAmount == null ? BigDecimal.ZERO : heldAmount;
    }
}
