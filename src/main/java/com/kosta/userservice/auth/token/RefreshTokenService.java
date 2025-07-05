package com.kosta.userservice.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_TTL = 7 * 24 * 60 * 60;

    public void saveRefreshToken(String userEmail, String refreshToken) {
        redisTemplate.opsForValue().set(userEmail, refreshToken, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String userEmail) {
        return redisTemplate.opsForValue().get(userEmail);
    }

    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(userEmail);
    }

    public boolean validateRefreshToken(String userEmail, String refreshToken) {
        String storedToken = getRefreshToken(userEmail);
        return storedToken != null && storedToken.equals(refreshToken);
    }

}
