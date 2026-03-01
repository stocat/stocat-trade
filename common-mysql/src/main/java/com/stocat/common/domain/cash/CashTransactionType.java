package com.stocat.common.domain.cash;

import lombok.Getter;

@Getter
public enum CashTransactionType {
    DEPOSIT("입금"),
    WITHDRAW("출금");

    private final String description;

    CashTransactionType(String description) {
        this.description = description;
    }
}
