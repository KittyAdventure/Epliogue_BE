package com.team1.epilogue.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.JwtAuthenticationWebFilter;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import java.util.List;

import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import com.team1.epilogue.auth.security.JwtAuthenticationFilter;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;

  /**
   * [메서드 레벨]
   * BCryptPasswordEncoder를 사용하여 PasswordEncoder 빈 등록
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * [메서드 레벨]
   * CustomUserDetailsService를 UserDetailsService 빈으로 등록 (구현체는 CustomUserDetailsService)
   */
  @Bean
  public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
    return customUserDetailsService;
  }

  /**
   * [메서드 레벨]
   * DaoAuthenticationProvider를 사용하여 사용자 인증을 처리하는 인증 제공자 빈 생성
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                          PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  /**
   * [메서드 레벨]
   * AuthenticationManager 빈 생성. ProviderManager를 사용하여 인증 제공자 목록을 등록
   */
  @Bean
  public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
    return new ProviderManager(List.of(authenticationProvider));
  }

  /**
   * [메서드 레벨]
   * HttpSecurity를 사용하여 보안 필터 체인 구성.
   * - CSRF 비활성화, 세션은 STATELESS, 특정 URL은 permitAll, 그 외는 인증 필요
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.disable())  // CORS 비활성화 (필요시 수정)
            .csrf(csrf -> csrf.disable())   // CSRF 비활성화
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/members/register",
                            "/api/members/login",
                            "/api/members/login/social",
                            "/api/members/auth/kakao/callback",
                            "/api/members/auth/google/callback",
                            "/login/oauth2/**",    // OAuth2 로그인 콜백 URL 허용
                            "/api/books/**",
                            "/api/kp/success",
                            "/api/kp/fail",
                            "/api/kp/cancel"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationWebFilter(jwtTokenProvider,memberRepository), UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(withDefaults())  // OAuth2 로그인 활성화 (기본 설정 적용)
            .httpBasic(httpBasic -> httpBasic.disable());

    return http.build();
  }
}