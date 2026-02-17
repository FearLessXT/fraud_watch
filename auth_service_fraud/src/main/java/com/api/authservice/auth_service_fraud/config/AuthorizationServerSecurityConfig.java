package com.api.authservice.auth_service_fraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthorizationServerSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        var authServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http.apply(authServerConfigurer);

        var endpointMatchers = authServerConfigurer.getEndpointsMatcher();

        http
                .securityMatcher(endpointMatchers)
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().permitAll()
                )
                .csrf(
                        csrf -> csrf.ignoringRequestMatchers(endpointMatchers)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(
                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultFilterChain(HttpSecurity http){
        http
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().denyAll()
                )
                .csrf(
                        AbstractHttpConfigurer::disable
                );
        return http.build();
    }
}
