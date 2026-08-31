package com.api.fraudaction.auth_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoderFactory;

@Configuration
public class JwtDecoderConfig {

    @Value("${auth.service.url:http://localhost:9001}")
    private String authServiceUrl;

    @Bean
    public ReactiveJwtDecoderFactory<?> reactiveJwtDecoderFactory() {
        return (clientId) -> jwtDecoder();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(authServiceUrl + "/.well-known/jwks.json")
                .build();
    }
}