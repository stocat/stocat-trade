package com.stocat.tradeapi.cash.service;

import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashBalanceEntity;
import com.stocat.common.domain.cash.CashHoldingEntity;
import com.stocat.common.domain.cash.CashHoldingStatus;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import com.stocat.common.exception.ApiException;
import com.stocat.common.repository.CashBalanceRepository;
import com.stocat.common.repository.CashHoldingRepository;
import com.stocat.common.repository.CashTransactionRepository;
import com.stocat.tradeapi.exception.TradeErrorCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CashCommandService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CashHoldingRepository cashHoldingRepository;
    private final CashTransactionRepository cashTransactionRepository;

    public CashBalanceEntity createWallet(Long userId, Currency currency) {
        if (cashBalanceRepository.findByUserIdAndCurrency(userId, currency).isPresent()) {
            throw new ApiException(TradeErrorCode.CASH_BALANCE_ALREADY_EXISTS);
        }
        CashBalanceEntity entity = CashBalanceEntity.builder()
                .userId(userId)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .build();
        return cashBalanceRepository.save(entity);
    }

    public Long createCashHolding(Long cashBalanceId, BigDecimal amount) {
        validateAmount(amount);

        CashBalanceEntity balance = getBalanceForUpdate(cashBalanceId);
        balance.reserve(amount);

        CashHoldingEntity holding = cashHoldingRepository.save(CashHoldingEntity.hold(balance.getId(), amount));
        return holding.getId();
    }

    public void consumeHolding(Long holdingId) {
        CashHoldingEntity holding = getHoldingForUpdate(holdingId);
        CashBalanceEntity balance = getBalanceForUpdate(holding.getCashBalanceId());

        if (holding.getStatus() != CashHoldingStatus.HOLD) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_ALREADY_FINALIZED);
        }
        holding.consume();

        validateAmount(holding.getAmount());
        validateAmount(balance.getBalance());
        if (balance.getReservedBalance().compareTo(holding.getAmount()) < 0) {
            throw new ApiException(TradeErrorCode.INSUFFICIENT_CASH_BALANCE);
        }
        balance.settleReservedAmount(holding.getAmount());
        saveTransactionHistory(balance, holding.getAmount(), CashTransactionType.WITHDRAW);
    }

    /**
     * 현금 홀딩 해제 (Release)
     * <p>
     * 1. 홀딩 상태가 유효한지(HOLD) 확인합니다. 2. 연결된 현금 잔액(CashBalance)에서 예약된 금액(Reserved Balance)을 차감합니다. 3. 홀딩 상태를 RELEASED 로
     * 삭제합니다.
     * </p>
     *
     * @param holdingId 해제할 홀딩 ID
     */
    public void releaseHolding(Long holdingId) {
        // 홀딩 정보 조회
        CashHoldingEntity holding = getHoldingForUpdate(holdingId);

        // 이미 처리된 홀딩인지 체크
        if (holding.getStatus() != CashHoldingStatus.HOLD) {
            throw new ApiException(TradeErrorCode.CASH_HOLDING_ALREADY_FINALIZED);
        }

        // 잔액 정보 조회 및 예약금 차감
        CashBalanceEntity balance = getBalanceForUpdate(holding.getCashBalanceId());
        balance.cancelReservation(holding.getAmount());

        // 홀딩 상태 변경
        holding.release();
    }

    private void saveTransactionHistory(CashBalanceEntity balance, BigDecimal amount, CashTransactionType type) {
        CashTransactionEntity transaction = CashTransactionEntity.create(
                balance.getUserId(),
                balance.getCurrency(),
                amount,
                balance.getBalance(),
                type
        );
        cashTransactionRepository.save(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(TradeErrorCode.INVALID_CASH_AMOUNT);
        }
    }

    private CashBalanceEntity getBalanceForUpdate(Long cashBalanceId) {
        return cashBalanceRepository.findByIdForUpdate(cashBalanceId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_BALANCE_NOT_FOUND));
    }

    private CashHoldingEntity getHoldingForUpdate(Long holdingId) {
        return cashHoldingRepository.findByIdForUpdate(holdingId)
                .orElseThrow(() -> new ApiException(TradeErrorCode.CASH_HOLDING_NOT_FOUND));
    }
}
