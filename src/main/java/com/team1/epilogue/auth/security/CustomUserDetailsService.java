package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
/**
 * [클래스 레벨]
 * 동기 방식으로 사용자 정보를 조회하는 UserDetailsService 구현체.
 */
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * [필드 레벨]
     * 회원 정보를 조회하는 Repository
     */
    private final MemberRepository memberRepository;

    /**
     * [생성자 레벨]
     * MemberRepository를 주입받아 사용자 정보를 조회할 수 있도록 설정
     *
     * @param memberRepository 사용자 정보를 관리하는 Repository
     */
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * [메서드 레벨]
     * 주어진 사용자 ID(username)을 기반으로 UserDetails 정보 조회
     *
     * @param username 사용자 로그인 ID
     * @return UserDetails 객체 (CustomMemberDetails) 반환
     * @throws UsernameNotFoundException 사용자 정보가 없으면 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return CustomMemberDetails.fromMember(member);
    }
}
