package com.stocat.common.domain.order;

public enum OrderStatus {
    SUBMITTED,
    PENDING,
    FILLED,
    CANCELLED,
    REJECTED
    ;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case SUBMITTED -> next == PENDING || next == CANCELLED;
            case PENDING -> next == FILLED || next == CANCELLED || next == REJECTED;
            default -> false;
        };
    }
}
