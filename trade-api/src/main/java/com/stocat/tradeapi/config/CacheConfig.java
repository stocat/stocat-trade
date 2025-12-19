package com.stocat.tradeapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String ASSETS = "assetsById";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(
                List.of(assetsCache())
        );
        return manager;
    }

    @Bean
    public CaffeineCache assetsCache() {
        return new CaffeineCache(
                ASSETS,
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(12, TimeUnit.HOURS)
                        .recordStats()
                        .build()
        );
    }
}
