package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.repository.MemberRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
/**
 * [클래스 레벨]
 * Reactive 방식으로 사용자 정보를 조회하는 UserDetailsService 구현체
 * Spring Security에서 사용자의 인증 정보를 불러오는 역할 담당
 */
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    /**
     * [필드 레벨]
     * 사용자 정보를 조회하는 Repository
     */
    private final MemberRepository memberRepository;

    /**
     * [생성자 레벨]
     * MemberRepository를 주입받아 사용자 정보를 조회할 수 있도록 설정
     *
     * @param memberRepository 사용자 정보를 관리하는 Repository
     */
    public CustomReactiveUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * [메서드 레벨]
     * 사용자의 로그인 ID(username)를 기반으로 UserDetails 정보 조회
     * Spring Security에서 사용자 인증을 처리할 때 호출
     *
     * @param username 로그인 ID
     * @return Mono<UserDetails> - 조회된 사용자 정보를 포함한 Mono 객체
     *         존재하지 않을 경우 UsernameNotFoundException 예외 발생
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> memberRepository.findByLoginId(username)) // 비동기적으로 사용자 조회
                .flatMap(optionalMember -> optionalMember
                        .map(member -> Mono.just(
                                User.withUsername(member.getLoginId()) // Security User 객체 생성
                                        .password(member.getPassword()) // 비밀번호 설정
                                        .roles("USER") // 기본 역할 부여
                                        .build()))
                        .orElseGet(() -> Mono.error(new UsernameNotFoundException("User not found: " + username)))); // 유저가 없을 경우 예외 처리
    }
}
