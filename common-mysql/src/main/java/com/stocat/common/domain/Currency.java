package com.stocat.common.domain;

import java.util.Arrays;

public enum Currency {
    KRW,
    USD,
    BTC,
    USDT;

    public static Currency fromMarket(String market) {
        String base = market.split("-")[0];
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(base))
                .findFirst()
                .orElse(KRW);
    }
}
