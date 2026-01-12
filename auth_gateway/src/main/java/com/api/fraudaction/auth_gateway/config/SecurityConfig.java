package com.api.fraudaction.auth_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@SuppressWarnings("all")
public class SecurityConfig {

    /**
     * Auth Endpoint - no jwt
     */
    @Bean
    @Order(0)
    SecurityWebFilterChain authEndpointSecurity(ServerHttpSecurity http) {
        return http
                .securityMatcher(
                        ServerWebExchangeMatchers.pathMatchers(
                                "/oauth2/token",
                                "/oauth2/jwks"
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
                                .pathMatchers("/api/transaction/**")
                                .hasAuthority("SCOPE_client.write")
                                .anyExchange().authenticated()

                )
                .oauth2ResourceServer(o -> o.jwt(jwt->{}))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
