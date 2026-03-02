package com.stocat.common.redis.repository;

import com.stocat.common.redis.constants.ExchangeRateKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExchangeRateRedisRepository {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Optional<BigDecimal> findRate(String currencyPair) {
        String key = ExchangeRateKeys.rateKey(currencyPair);
        String value = redisTemplate.opsForValue().get(key).block();
        return Optional.ofNullable(value).map(BigDecimal::new);
    }
}