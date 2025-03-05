package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;

    public void invalidate(String token) {
        Date expiration = jwtTokenProvider.extractExpiration(token);
        long now = new Date().getTime();
        long duration = expiration.getTime() - now;
        if(duration > 0) {
            redisUtil.setBlackList(token, "logout", Duration.ofMillis(duration));
        }
    }
}