package com.apigateway.filter;

import com.apigateway.config.PublicEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtBypassWebFilter implements WebFilter {

    private final PublicEndpointConfig publicEndpointConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    public JwtBypassWebFilter(PublicEndpointConfig publicEndpointConfig) {
        this.publicEndpointConfig = publicEndpointConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        boolean isPublicEndpoint = publicEndpointConfig.getPublicEndpoints()
                .stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
        boolean isPublicGet = "GET".equalsIgnoreCase(method) &&
                publicEndpointConfig.getPublicGetEndpoints()
                        .stream().anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (isPublicEndpoint || isPublicGet) {
            ServerHttpRequest mutated = exchange.getRequest()
                    .mutate()
                    .headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        return chain.filter(exchange);
    }
}
