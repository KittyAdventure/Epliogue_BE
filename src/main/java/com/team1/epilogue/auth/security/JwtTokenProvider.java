package com.team1.epilogue.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * [클래스 레벨]
 * JWT 토큰의 생성 및 검증을 담당하는 클래스.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * [필드 레벨]
     * JWT_EXPIRATION: JWT 토큰의 유효 기간 (7일 = 604800000 밀리초)
     */
    private final long JWT_EXPIRATION = 604800000L;

    /**
     * [필드 레벨]
     * HS512에 적합한 512비트(64바이트) 랜덤 시크릿 키 생성
     */
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public JwtTokenProvider() {
        log.info("생성된 JWT Secret Key (Base64) : {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    /**
     * [메서드 레벨]
     * JWT 토큰을 생성하는 메서드
     *
     * @param memberId 사용자 ID
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * [메서드 레벨]
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getMemberIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * [메서드 레벨]
     * JWT 토큰의 유효성을 검증하는 메서드
     *
     * @param token JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
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
