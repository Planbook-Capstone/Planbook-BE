package com.partner.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class HeaderAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String username = request.getHeaders().getFirst("X-Username");
        String userId = request.getHeaders().getFirst("X-User-Id");
        String role = request.getHeaders().getFirst("X-User-Role");

        if (username != null && !username.isBlank() && userId != null && !userId.isBlank() && role != null && !role.isBlank()) {
            var authorities = List.of(new SimpleGrantedAuthority(role));
            var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            return Mono.just(auth);
        }
        return Mono.empty();
    }
}

