package com.team1.epilogue.auth.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * [클래스 레벨]
 * JwtAuthenticationWebFilter는 들어오는 HTTP 요청에서 JWT 토큰을 추출하여,
 * 해당 토큰이 유효하면 인증 객체(Authentication)를 생성 후 SecurityContext에 설정하는 WebFilter
 * 우선순위가 높도록 @Order(-100)로 설정
 */
@Component
@Order(-100) // 낮은 값일수록 높은 우선순위
public class JwtAuthenticationWebFilter implements WebFilter {

    /**
     * [필드 레벨]
     * tokenProvider: JWT 토큰의 생성, 검증, 파싱 등의 기능을 제공하는 컴포넌트
     * 생성자 주입을 통해 초기화됨
     */
    private final JwtTokenProvider tokenProvider;

    /**
     * [생성자 레벨]
     * JwtAuthenticationWebFilter 생성자는 JwtTokenProvider를 주입받아 필드를 초기화
     *
     * @param tokenProvider JWT 토큰 처리 컴포넌트
     */
    public JwtAuthenticationWebFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * [메서드 레벨]
     * filter 메서드는 HTTP 요청에서 JWT 토큰을 추출하고, 토큰이 유효하면 인증 객체를 SecurityContext에 설정한 후
     * 다음 필터 체인으로 요청을 전달
     *
     * @param exchange 현재 HTTP 요청/응답 정보를 담은 ServerWebExchange 객체
     * @param chain 다음 WebFilterChain
     * @return 다음 필터 체인으로 요청을 전달하는 Mono<Void> 객체를 반환
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = getJwtFromRequest(exchange);
        if (token != null && tokenProvider.validateToken(token)) {
            String memberId = tokenProvider.getMemberIdFromJWT(token);
            Authentication auth = new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }
        return chain.filter(exchange);
    }

    /**
     * [메서드 레벨]
     * getJwtFromRequest 메서드는 HTTP 요청의 Authorization 헤더에서 "Bearer " 접두사를 제거한 JWT 토큰을 추출
     *
     * @param exchange 현재 HTTP 요청 정보를 담은 ServerWebExchange 객체
     * @return JWT 토큰 문자열이 있으면 반환하고, 없으면 null을 반환
     */
    private String getJwtFromRequest(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
