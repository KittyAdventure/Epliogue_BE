package com.team1.epilogue.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

//  /**
//   * [내부 클래스 - 테스트 전용 보안 설정] TestSecurityConfig 클래스는 테스트 환경에서 보안 관련 설정을 커스터마이징하기 위한 설정 클래스로, 모든 HTTP
//   * 요청에 대해 인증 없이 접근할 수 있도록 CSRF 보호를 비활성화하고, 모든 요청을 허용
//   */
//  @Bean
//  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//    return http
//        .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
//        .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll()) // 모든 요청 허용
//        .build();
//  }
}