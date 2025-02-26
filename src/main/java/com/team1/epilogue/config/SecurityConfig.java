package com.team1.epilogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * 클래스 레벨:
 * SecurityConfig 클래스는 애플리케이션의 보안 설정을 구성하는 클래스
 * 이 클래스는 WebFlux 기반 보안을 활성화하고, HTTP 요청에 대한 인증 및 권한 부여 정책을 정의
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 메서드 레벨:
     * securityWebFilterChain 메서드는 ServerHttpSecurity 객체를 사용하여 보안 필터 체인을 구성
     * - CSRF 보호를 비활성화
     * - "/api/members/register" 경로는 인증 없이 접근할 수 있도록 허용
     * - 그 외 모든 요청은 인증을 필요로 함
     * - HTTP Basic 인증을 기본 설정으로 사용
     *
     * @param http ServerHttpSecurity 객체로, HTTP 보안 설정을 구성하는데 사용
     * @return 구성된 SecurityWebFilterChain 객체를 반환
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/members/register").permitAll() // 해당 경로는 인증 없이 접근 가능
                        .anyExchange().authenticated() // 그 외 모든 요청은 인증을 필요
                )
                .httpBasic(withDefaults()) // 기본 HTTP Basic 인증을 사용
                .build(); // 보안 필터 체인을 구성하여 반환
    }
}
