package com.stocat.trade.websocket.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSubscriberService {
    private final ReactiveRedisMessageListenerContainer redisContainer;
    private final ChannelTopic cryptoTradesTopic;

    /**
     * "crypto:trades" 채널 메시지를 실시간으로 스트리밍합니다.
     */
    public Flux<String> subscribeTrades() {
        return redisContainer.receive(cryptoTradesTopic)
                .doOnSubscribe(sub -> log.debug("Redis 토픽 {} 구독 시작", cryptoTradesTopic.getTopic()))
                .map(ReactiveSubscription.Message::getMessage)
                .doOnCancel(() -> log.debug("Redis 토픽 {} 구독 종료", cryptoTradesTopic.getTopic()))
                .doOnError(e -> log.error("Redis 구독 오류", e));
    }
}
