package com.api.fraudaction.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthorizationServerSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        var authServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http.apply(authServerConfigurer);

        var endpointsMatchers = authServerConfigurer.getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatchers)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(
                        csrf -> csrf.ignoringRequestMatchers(endpointsMatchers)
                )
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {

        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
