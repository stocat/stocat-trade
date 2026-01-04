package com.stocat.common.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("체결대기"),
    FILLED("체결"),
    CANCELED("주문취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public boolean canTransitionTo(OrderStatus next) {
        if (this == PENDING) {
            return next == FILLED || next == CANCELED;
        }
        return false;
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}
