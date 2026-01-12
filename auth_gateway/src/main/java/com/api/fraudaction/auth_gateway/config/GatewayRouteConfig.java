package com.api.fraudaction.auth_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    RouteLocator gatewayRoutes(
            RouteLocatorBuilder builder,
            RedisRateLimiter redisRateLimiter,
            KeyResolver ipKeyResolver
    ) {
        return builder.routes()

                //Auth Service routes

                // Oauth2 Token Endpoint
                .route("auth-token", r -> r
                        .path("/oauth2/token")
                        .filters( f->f
                                .circuitBreaker(c->c
                                        .setName("auth-token")
                                        .setFallbackUri("forward:/fallback/auth")
                                )
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(ipKeyResolver)
                                )
                        )
                        .uri("localhost:8001")
                )

                //TRANSACTION INGESTION
                .route("transaction-ingestion", r -> r
                        .path("/api/transaction/**")
                        .filters( f -> f
                                .filter(new CorrelationIdFilter())
                                .requestRateLimiter( c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(ipKeyResolver)
                                )
                        )
                        .uri("localhost:8002")
                )
                .build();

    }
}
