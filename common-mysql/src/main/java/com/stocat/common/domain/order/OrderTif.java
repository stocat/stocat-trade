package com.stocat.common.domain.order;

import lombok.Getter;

@Getter
public enum OrderTif {
    GTC("취소 전까지 유효"), IOC("미체결 수량 자동 취소");

    private final String description;

    OrderTif(String description) {
        this.description = description;
    }

    public static OrderTif from(String type) {
        return switch (type) {
            case "GTC" -> GTC;
            case "IOC" -> IOC;
            default -> throw new IllegalArgumentException("Invalid order tif: " + type);
        };
    }
}
