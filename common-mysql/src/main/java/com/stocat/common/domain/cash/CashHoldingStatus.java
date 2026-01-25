package com.stocat.common.domain.cash;

public enum CashHoldingStatus {
    HELD("현금 홀딩"),
    CONSUMED("체결로 사용"),
    RELEASED("취소/만료로 해제");

    private final String description;

    CashHoldingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
