package com.team1.epilogue.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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





////방글 테스트
//
//package com.team1.epilogue.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class TestSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.disable())
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/members/register",
//                                "/api/members/login",
//                                "/api/members/login/social",
//                                "/login/oauth2/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                            response.getWriter().write("{\"상태\":401,\"에러코드\":\"UNAUTHORIZED\",\"메시지\":\"인증되지 않은 사용자\"}");
//                        })
//                )
//                .oauth2Login(withDefaults())
//                .httpBasic(httpBasic -> httpBasic.disable());
//
//        return http.build();
//    }
//}