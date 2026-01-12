package com.api.fraudaction.auth_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ComponentScan
@SuppressWarnings("all")
public class CorrelationIdFilter implements GatewayFilter {

    private static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var headers = exchange.getRequest().getHeaders();
        var correlatonId =
                headers.getFirst(CORRELATION_ID) != null ?
                        headers.getFirst(CORRELATION_ID) :
                        UUID.randomUUID().toString();

        var mutedRequest = exchange
                .getRequest()
                .mutate()
                .header(CORRELATION_ID, correlatonId)
                .build();

        return chain.filter(exchange.mutate().request(mutedRequest).build());
    }
}
