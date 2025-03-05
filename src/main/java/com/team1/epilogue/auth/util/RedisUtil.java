package com.team1.epilogue.auth.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;


/**
 * [클래스 레벨]
 * Redis 관련 유틸리티 클래스
 * - JWT 블랙리스트 관리 및 일반적인 Redis 활용 기능 제공
 * - 토큰 무효화, 키-값 저장 및 조회, 삭제 기느ㅇ 포함
 */
@Component
public class RedisUtil {

    // Redis 데이터 저장 및 조회를 위한 StringRedisTemplate
    private final StringRedisTemplate redisTemplate;

    /**
     * [생성자 레벨]
     * RedisUtil 생성자
     * @param redisTemplate Spring Data Redis에서 제공하는 템플릿 객체
     */
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * [메서드 레벨]
     * JWT 블랙리스트에 토큰을 저장하여 무효화 처리
     * @param token 블랙리스트에 추가할 JWT 토큰
     * @param value 저장할 값 (예: "logout")
     * @param duration 토큰이 블랙리스트에 유지될 시간 (토큰 만료 시간까지)
     */
    public void setBlackList(String token, String value, Duration duration) {
        redisTemplate.opsForValue().set(token, value, duration);
    }

    /**
     * [메서드 레벨]
     * Redis에서 특정 키의 값을 조회
     * @param key 조회할 키
     * @return 해당 키의 값 (없으면 null 반환)
     */
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * [메서드 렙벨]
     * Redis에서 특정 키를 삭제
     * @param key 삭제할 키
     */
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
