package com.BE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final static long REFRESH_TOKEN_TTL = 30L;

    public void saveRefreshToken(String refreshToken, UUID id) {
        redisTemplate.opsForValue().set(
                refreshToken,
                id.toString(),
                Duration.ofDays(REFRESH_TOKEN_TTL)
        );
    }


    public UUID getIdFromRefreshToken(String refreshToken) {
        String idStr = (String) redisTemplate.opsForValue().get(refreshToken);
        return idStr != null ? UUID.fromString(idStr) : null;
    }


    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }


    public boolean validateRefreshToken(String refreshToken) {
        return redisTemplate.hasKey(refreshToken);
    }
}
