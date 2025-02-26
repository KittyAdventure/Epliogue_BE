package com.team1.epilogue.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  /**
   * 메서드 레벨:
   * passwordEncoder 메서드는 BCryptPasswordEncoder를 사용하여 PasswordEncoder 빈을 생성
   * BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하기 위해 널리 사용되는 해시 알고리즘
   *
   * @return BCryptPasswordEncoder 인스턴스를 PasswordEncoder 타입으로 반환
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

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
