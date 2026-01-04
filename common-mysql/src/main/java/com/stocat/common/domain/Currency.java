package com.stocat.common.domain;

import java.util.Arrays;

public enum Currency {
    KRW,
    USD,
    BTC,
    USDT;

    public AssetsCategory toCashCategory() {
        return switch (this) {
            case KRW -> AssetsCategory.KRW;
            case USD -> AssetsCategory.USD;
            default -> throw new IllegalArgumentException();
        };
    }
}
