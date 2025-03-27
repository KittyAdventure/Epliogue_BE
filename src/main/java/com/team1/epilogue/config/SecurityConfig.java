package com.team1.epilogue.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import com.team1.epilogue.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;

  /** cors 설정 bean */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");
    configuration.addExposedHeader("x-auth-token");
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  // 사용자의 비밀번호 암호화, 인증시 입력된 비밀번호 비교
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  //UserDetailsService + DaoAuthenticationProvider 방식 구현
  // 로그인 시 입력한 사용자 id기반 사용자 조회해 인증정보 제공
  @Bean
  public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
    return customUserDetailsService;
  }

  //로그인 요청 시 사용자의 자격 증명 검증해 인증 수행
  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  // 로그인 시, 사용자 인증 요청을 처리해서 올바른 인증 객체를 생성
  @Bean
  public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
    return new ProviderManager(List.of(authenticationProvider));
  }

  /**
   * HTTP 보안 규칙을 정의
   * - 서버는 상태(세션)를 유지하지 않음
   * - CSRF 보호와 기본 HTTP 인증을 사용하지 않음
   * - 특정 URL은 인증 없이 접근할 수 있으며, 그 외의 요청은 인증필요
   * - JWT 토큰을 통해 인증을 우선 처리하며, OAuth2 로그인 지원
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/members/register",
                            "/api/members/login",
                            "/api/members/login/social",
                            "/api/members/auth/kakao/callback",
                            "/api/members/auth/google/callback",
                            "/api/members/search",
                            "/login/oauth2/**",    // OAuth2 로그인 콜백 URL 허용
                            "/api/books/**",
                            "/api/kp/success",
                            "/api/kp/fail",
                            "/api/kp/cancel",
                            "/api/share",
                            "/api/share/**",
                            "/api/trending-books",
                            "/api/books/main-page",
                            "/api/keywords",
                            "/api/mypage/calendar",
                            "/api/mypage/reviews",
                            "/api/comments/view",
                            "/api/mypage/meeting",
                            "/api/item/bought",
                            "/api/item/list",
                            "/api/meeting/chat/**",
                            "/api/meeting/**",
                            "/api/meetings/gatherings/**",
                            "/api/books/detail",
                            "/api/reviews/latest",
                            "/api/books/{bookId}/reviews",
                            "/api/reviews/**",
                            "/api/mypage/user-info"
                    ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/books/{bookId}/reviews").authenticated()
                    .anyRequest().authenticated()

            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(withDefaults()).exceptionHandling( ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(
            HttpStatus.UNAUTHORIZED)))
            .httpBasic(httpBasic -> httpBasic.disable());
    return http.build();
  }
}