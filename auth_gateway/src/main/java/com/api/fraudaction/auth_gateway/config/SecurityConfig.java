package com.api.fraudaction.auth_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@SuppressWarnings("all")
public class SecurityConfig {

    private final ReactiveJwtDecoder jwtDecoder;

    public SecurityConfig(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Auth Endpoint - no jwt
     */
    @Bean
    @Order(0)
    SecurityWebFilterChain authEndpointSecurity(ServerHttpSecurity http) {
        return http
                .securityMatcher(
                        ServerWebExchangeMatchers.pathMatchers(
                                "/token",
                                "/jwks",
                                "/.well-known/**"
                        )
                )
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    /**
     * Jwt Endpoint - jwt required
     */
    @Bean
    @Order(1)
    SecurityWebFilterChain apiSecurity(ServerHttpSecurity apiSecurity) {
        return apiSecurity
                .securityMatcher(
                        ServerWebExchangeMatchers.pathMatchers(
                                "/api/**"
                        )
                )
                .authorizeExchange(
                        ex -> ex
                                .pathMatchers("/api/transactions/**")
                                .hasAuthority("SCOPE_client.write")
                                .anyExchange().authenticated()

                )
                .oauth2ResourceServer(o -> o.jwt(jwt->jwt.jwtDecoder(jwtDecoder)))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
