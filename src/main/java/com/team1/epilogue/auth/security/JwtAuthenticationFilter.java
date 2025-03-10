package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


/**
 * [클래스 레벨]
 * JWT 인증 필터
 * OncePerRequestFilter를 상속받아, 모든 요청에서 한 번만 실행됨
 * JWT 토큰을 검증하고 인증 정보를 SecurityContext에 저장하는 역할 수행
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * [필드 레벨]
     * JWT 토큰을 생성하고 검증하는 JwtTokenProvider
     */
    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    /**
     * [생성자 레벨]
     * JwtTokenProvider를 주입받아 초기화
     *
     * @param tokenProvider JWT 토큰 처리 객체
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    /**
     * [메서드 레벨]
     * 요청이 들어올 때마다 실행되는 필터 메서드
     * - 요청 헤더에서 JWT 토큰을 추출
     * - 토큰 검증 후 사용자 인증 정보(SecurityContextHolder) 설정
     * - 다음 필터로 요청을 전달
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 발생하는 예외
     * @throws IOException 입출력 예외 발생 시 처리
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (token != null && tokenProvider.validateToken(token)) {
            String memberIdStr = tokenProvider.getMemberIdFromJWT(token);
            Member member = memberRepository.findById(Long.parseLong(memberIdStr))
                    .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
            CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }


    /**
     * [메서드 레벨]
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 (Bearer 토큰 형식에서 "Bearer " 부분 제거)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Authorization 헤더가 존재하고 "Bearer "로 시작하는 경우 토큰 추출
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
