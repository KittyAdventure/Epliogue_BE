package com.team1.epilogue.authfix.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * [클래스 레벨]
 * JwtTokenProvider는 JWT 토큰을 생성, 검증, 파싱하는 역할을 하는 컴포넌트
 * 이 클래스는 사용자의 ID를 기반으로 JWT 토큰을 생성하고, 토큰에서 사용자 ID를 추출하며,
 * 토큰의 유효성을 검사
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * [필드 레벨]
     * jwtSecret: JWT 토큰의 서명(Signature)을 생성 및 검증할 때 사용되는 비밀 키
     * application.properties 또는 application.yml 파일에 설정된 값을 주입받음
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * [필드 레벨]
     * JWT_EXPIRATION: JWT 토큰의 유효 기간을 나타내는 상수
     * 여기서는 7일(604800000 밀리초) 동안 유효한 토큰을 생성
     */
    private final long JWT_EXPIRATION = 604800000L;

    /**
     * [메서드 레벨]
     * generateToken: 주어진 사용자 ID를 기반으로 JWT 토큰을 생성
     *
     * @param memberId 토큰의 subject로 사용할 사용자 ID
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(memberId)      // 토큰의 subject에 사용자 ID 설정
                .setIssuedAt(now)        // 토큰 발급 시각 설정
                .setExpiration(expiryDate)  // 토큰 만료 시각 설정
                .signWith(SignatureAlgorithm.HS512, jwtSecret)  // HS512 알고리즘과 비밀키를 사용해 서명
                .compact();              // JWT 토큰을 문자열로 압축하여 반환
    }

    /**
     * [메서드 레벨]
     * getMemberIdFromJWT: JWT 토큰에서 사용자 ID를 추출
     *
     * @param token JWT 토큰 문자열
     * @return 토큰의 subject에 해당하는 사용자 ID
     */
    public String getMemberIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * [메서드 레벨]
     * validateToken: 주어진 JWT 토큰의 유효성을 검사
     * 토큰이 만료되었거나, 지원하지 않는 토큰, 형식이 잘못된 토큰, 서명이 유효하지 않은 토큰,
     * 클레임이 비어있는 토큰 등에 대해 로그를 기록하고 false를 반환
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("지원하지 않는 JWT 토큰입니다: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("잘못된 JWT 토큰입니다: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("JWT 토큰의 서명이 유효하지 않습니다: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰의 클레임이 비어 있습니다: {}", ex.getMessage());
        }
        return false;
    }
}
