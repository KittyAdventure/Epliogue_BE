package com.team1.epilogue.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * [필드 레벨]
     * jwtSecret: JWT 서명(Signature)에 사용될 비밀 키
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * [필드 레벨]
     * JWT_EXPIRATION: JWT 토큰의 유효 기간 (7일 = 604800000 밀리초)
     */
    private final long JWT_EXPIRATION = 604800000L;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(String memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(memberId)  // 사용자 ID 저장
                .setIssuedAt(now)  // 발급 시간
                .setExpiration(expiryDate)  // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // 서명
                .compact();
    }

    public String getMemberIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // 동일한 서명 키 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
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
