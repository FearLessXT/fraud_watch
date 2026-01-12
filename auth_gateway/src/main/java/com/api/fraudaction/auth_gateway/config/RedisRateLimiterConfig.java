package com.api.fraudaction.auth_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisRateLimiterConfig {

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 10);
    }
}
