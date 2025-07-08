package com.partner.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<String> getAccessToken(String toolName, String tokenUrl, String clientId, String clientSecret) {
        ReactiveValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = "token:" + toolName;

        return ops.get(key).switchIfEmpty(
                WebClient.create()
                        .post()
                        .uri(tokenUrl)
                        .bodyValue(Map.of("client_id", clientId, "client_secret", clientSecret, "grant_type", "client_credentials"))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(res -> {
                            String token = (String) res.get("access_token");
                            Integer expiresIn = (Integer) res.get("expires_in");
                            return Map.of("token", token, "expires_in", expiresIn);
                        })
                        .flatMap(map -> {
                            String token = (String) map.get("token");
                            Integer expiresIn = (Integer) map.get("expires_in");
                            return ops.set(key, token, Duration.ofSeconds(expiresIn)).thenReturn(token);
                        })
        );
    }
}
