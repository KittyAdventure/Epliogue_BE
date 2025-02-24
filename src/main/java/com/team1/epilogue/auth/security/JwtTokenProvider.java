package com.team1.epilogue.auth.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 7일 동안 유효한 임시 토큰
    private final long JWT_EXPIRATION = 604800000L;

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            logger.error("만료된 JWT 토큰입니다: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("지원하지 않는 JWT 토큰입니다: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("잘못된 JWT 토큰입니다: {}", ex.getMessage());
        } catch (SignatureException ex) {
            logger.error("JWT 토큰의 서명이 유효하지 않습니다: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT 토큰의 클레임이 비어 있습니다: {}", ex.getMessage());
        }
        return false;
    }
}
