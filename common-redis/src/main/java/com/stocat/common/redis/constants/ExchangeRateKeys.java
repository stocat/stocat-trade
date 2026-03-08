package com.stocat.common.redis.constants;

public final class ExchangeRateKeys {

    private ExchangeRateKeys() {
    }

    private static final String EXCHANGE_RATE_PREFIX = "exchange:rate:";

    public static String rateKey(String currencyPair) {
        return EXCHANGE_RATE_PREFIX + currencyPair;
    }
}