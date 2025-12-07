package com.stocat.common.domain.order;

public enum OrderType {
    LIMIT, MARKET;

    public static OrderType from(String type) {
        return switch (type) {
            case "LIMIT" -> LIMIT;
            case "MARKET" -> MARKET;
            default -> throw new IllegalArgumentException("Invalid order type: " + type);
        };
    }
}
