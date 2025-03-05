package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;


/**
 * [클래스 레벨]
 * 로그아웃 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - JWT 토큰을 무효화하여 로그아웃을 처리
 * - Redis를 활용하여 블랙리스트 관리
 */
@Service
@RequiredArgsConstructor
public class LogoutService {

    // Redis를 활용한 블랙리스트 저장을 위한 유틸리티
    private final RedisUtil redisUtil;

    // JWT 토큰 관리 컴포넌트
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [메서드 레벨]
     * JWT 토큰을 블랙리스트에 추가하여 무효화하는 메서드
     * - 토큰의 만료 시간을 조회하여 해당 시간 동안 Redis에 저장
     * - 이후 동일한 토큰으로 요청이 오면 인증되지 않도록 처리
     *
     * @param token 로그아웃할 JWT 토큰
     */
    public void invalidate(String token) {
        // JWT 토큰의 만료 시간 추출
        Date expiration = jwtTokenProvider.extractExpiration(token);

        // 현재 시간과 비교하여 남은 유효 기간 계산
        long now = new Date().getTime();
        long duration = expiration.getTime() - now;

        // 만료되지 않은 토큰만 블랙리스트에 추가
        if(duration > 0) {
            redisUtil.setBlackList(token, "logout", Duration.ofMillis(duration));
        }
    }
}