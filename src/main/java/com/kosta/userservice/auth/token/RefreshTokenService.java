package com.kosta.userservice.auth.token;

import com.kosta.userservice.auth.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public void saveRefreshToken(String userEmail, String refreshToken) {
        long ttlSeconds = jwtProperties.getRefreshExpiration() / 1000;
        redisTemplate.opsForValue().set(userEmail, refreshToken, ttlSeconds, TimeUnit.SECONDS);
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
