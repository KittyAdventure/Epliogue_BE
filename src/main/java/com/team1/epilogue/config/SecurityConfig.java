package com.team1.epilogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /*
   * 패스워드 인코더 Bean 생성
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /*
   * 시큐리티 설정
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf((config) -> config.disable())
        .formLogin((form) -> form.disable());

    // 인증 작업 하지 않을 경로 설정
    http
        .authorizeHttpRequests((request) ->
            request.requestMatchers("/**")
                .permitAll());

    return http.build();
  }
}
