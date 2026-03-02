package com.stocat.common.redis.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import static com.stocat.common.redis.constants.CryptoKeys.CRYPTO_TRADES;

/**
 * Redis 연결 설정을 담당합니다.
 */
@Configuration
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class})
@Profile("!test")
public class RedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    /**
     * LettuceConnectionFactory는 Reactive/동기 템플릿을 모두 지원합니다.
     */
    @Primary
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration configuration) {
        return new LettuceConnectionFactory(configuration);
    }

    /** Pub/Sub 구독 등 리액티브 연산에 사용합니다. */
    @Bean
    public ReactiveStringRedisTemplate reactiveRedisTemplate(LettuceConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory, RedisSerializationContext.string());
    }

    /** 환율 조회, 환전 락 등 동기 연산에 사용합니다. */
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Primary
    @Bean
    public ReactiveRedisMessageListenerContainer redisContainer(
            ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisMessageListenerContainer(factory);
    }


    @Bean
    public ChannelTopic cryptoTradesTopic() {
        return new ChannelTopic(CRYPTO_TRADES);
    }
}
