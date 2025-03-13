package com.team1.epilogue.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final long JWT_EXPIRATION = 604800000L;
    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        // HS512 알고리즘은 최소 64바이트(512비트) 이상의 키가 필요합니다.
        if (secretBytes.length < 64) {
            throw new IllegalArgumentException("jwt.secret 값은 HS512 알고리즘을 위해 최소 64바이트 이상이어야 합니다.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        log.info("생성된 JWT 시크릿 키 (Base64): {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    public String generateToken(String memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        log.debug("generateToken - 사용 중인 시크릿 키: {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        String token = Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
        log.debug("생성된 토큰: {}", token);
        return token;
    }

    public String getMemberIdFromJWT(String token) {
        log.debug("getMemberIdFromJWT - 사용 중인 시크릿 키: {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.debug("파싱된 클레임: {}", claims);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        log.debug("extractExpiration - 사용 중인 시크릿 키: {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.debug("토큰 만료 시간: {}", claims.getExpiration());
        return claims.getExpiration();
    }

    public boolean validateToken(String token) {
        log.info("validateToken - 토큰 검증 시작: {}", token);
        log.info("validateToken - 사용 중인 시크릿 키: {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            log.debug("토큰이 유효합니다.");
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
