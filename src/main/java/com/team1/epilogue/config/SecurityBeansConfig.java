package com.team1.epilogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 클래스 레벨:
 * SecurityBeansConfig 클래스는 애플리케이션 전반에서 사용되는 보안 관련 빈들을 정의하는 구성 클래스
 * 이 클래스는 @Configuration 어노테이션을 사용하여 Spring 컨테이너에 빈을 등록
 */
@Configuration
public class SecurityBeansConfig {

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
}
