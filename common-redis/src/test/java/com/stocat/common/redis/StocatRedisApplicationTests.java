package com.stocat.common.redis;

import com.stocat.common.redis.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {RedisConfig.class})
class StocatRedisApplicationTests {

    @Test
    void contextLoads() {
    }

}
