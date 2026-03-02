package com.stocat.common.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocat.common.redis.constants.ExchangeRateLockKeys;
import com.stocat.common.redis.dto.ExchangeRateLock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExchangeRateLockRepository {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 환전 lock 데이터를 JSON으로 직렬화하여 TTL과 함께 저장하고 lockKey를 반환합니다.
     *
     * @param lock 저장할 환전 lock 데이터
     * @return 생성된 lockKey (UUID)
     */
    public String store(ExchangeRateLock lock) {
        String uuid = UUID.randomUUID().toString();
        String key = ExchangeRateLockKeys.lockKey(uuid);
        try {
            String json = objectMapper.writeValueAsString(lock);
            redisTemplate.opsForValue()
                    .set(key, json, ExchangeRateLockKeys.LOCK_TTL)
                    .block();
            return uuid;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("환전 lock 직렬화 실패", e);
        }
    }

    /**
     * lockKey에 해당하는 데이터를 역직렬화하여 반환하고 즉시 삭제합니다 (단일 사용 보장).
     *
     * @param uuid 미리보기 시 발급받은 lockKey
     * @return 환전 lock 데이터, 만료 또는 존재하지 않으면 empty
     */
    public Optional<ExchangeRateLock> getAndDelete(String uuid) {
        String key = ExchangeRateLockKeys.lockKey(uuid);
        String json = redisTemplate.opsForValue().getAndDelete(key).block();
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, ExchangeRateLock.class));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("환전 lock 역직렬화 실패", e);
        }
    }
}