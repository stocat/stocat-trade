package com.stocat.common.domain.order;

public enum OrderStatus {
    CREATED,
    PENDING,
    FILLED,
    CANCELED,
    REJECTED
    ;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> next == PENDING || next == CANCELED;
            case PENDING -> next == FILLED || next == CANCELED || next == REJECTED;
            default -> false;
        };
    }
}
