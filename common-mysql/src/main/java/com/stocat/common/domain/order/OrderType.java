package com.stocat.common.domain.order;

import lombok.Getter;

@Getter
public enum OrderType {
    LIMIT("지정가"), MARKET("시장가");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public static OrderType from(String type) {
        return switch (type) {
            case "LIMIT" -> LIMIT;
            case "MARKET" -> MARKET;
            default -> throw new IllegalArgumentException("Invalid order type: " + type);
        };
    }
}
