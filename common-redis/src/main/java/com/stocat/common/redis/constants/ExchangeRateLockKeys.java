package com.stocat.common.redis.constants;

import java.time.Duration;

public final class ExchangeRateLockKeys {

    private ExchangeRateLockKeys() {
    }

    private static final String EXCHANGE_RATE_LOCK_PREFIX = "exchange:rate-lock:";

    public static final Duration LOCK_TTL = Duration.ofSeconds(60);

    public static String lockKey(String uuid) {
        return EXCHANGE_RATE_LOCK_PREFIX + uuid;
    }
}