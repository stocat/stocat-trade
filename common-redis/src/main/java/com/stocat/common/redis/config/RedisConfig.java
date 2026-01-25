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
     * Redis 연결 팩토리를 생성합니다.
     *
     * @return LettuceConnectionFactory 인스턴스
     */
    @Primary
    @Bean
    public ReactiveRedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration configuration) {
        return new LettuceConnectionFactory(configuration);
    }

    /**
     * 문자열 기반 Redis 템플릿을 생성합니다.
     *
     * @param factory Redis 연결 팩토리
     * @return redisTemplate 인스턴스
     */
    @Primary
    @Bean
    public ReactiveStringRedisTemplate redisTemplate(
            ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(
                factory,
                RedisSerializationContext.string()
        );
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
