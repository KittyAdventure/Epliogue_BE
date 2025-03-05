package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.*;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * [클래스 레벨]
 * 인증 관련 비즈니스 로직을 구현한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleAuthService googleAuthService;
    private final KakaoAuthService kakaoAuthService;

    /**
     * [메서드 레벨]
     * 일반 로그인 처리
     *
     * @param request 일반 로그인 요청 데이터
     * @return 로그인 성공 시 JWT 토큰과 사용자 정보 반환
     * @throws BadCredentialsException 잘못된 아이디 또는 비밀번호 예외
     */
    @Override
    public LoginResponse login(GeneralLoginRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("아이디 혹은 패스워드가 틀립니다."));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("아이디 혹은 패스워드가 틀립니다.");
        }
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    /**
     * [메서드 레벨]
     * 소셜 로그인 처리 (Google, Kakao)
     *
     * @param request 소셜 로그인 요청 데이터
     * @return 로그인 성공 시 JWT 토큰과 사용자 정보 반환
     * @throws IllegalArgumentException 지원되지 않는 소셜 로그인 제공자 예외
     */
    @Override
    public LoginResponse socialLogin(SocialLoginRequest request) {
        Member member;
        if ("google".equalsIgnoreCase(request.getProvider())) {
            GoogleUserInfo googleUser = googleAuthService.getGoogleUserInfo(request.getAccessToken());
            member = findOrCreateMember(googleUser.getEmail(), googleUser.getSub(), googleUser.getName(), googleUser.getPicture(), "google");
        } else if ("kakao".equalsIgnoreCase(request.getProvider())) {
            KakaoUserInfo kakaoUser = kakaoAuthService.getKakaoUserInfo(request.getAccessToken());
            member = findOrCreateMember(kakaoUser.getKakao_account().getEmail(), String.valueOf(kakaoUser.getId()),
                    kakaoUser.getKakao_account().getProfile().getNickname(), kakaoUser.getKakao_account().getProfile().getProfileImageUrl(), "kakao");
        } else {
            throw new IllegalArgumentException("Unsupported social provider");
        }

        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    /**
     * [메서드 레벨]
     * 소셜 로그인 사용자의 회원 정보를 찾거나 신규 회원 생성
     */
    private Member findOrCreateMember(String email, String loginId, String name, String profileUrl, String socialType) {
        return memberRepository.findByEmail(email).orElseGet(() -> memberRepository.save(
                Member.builder()
                        .loginId(loginId)
                        .password("")
                        .nickname(name)
                        .name(name)
                        .email(email)
                        .phone("")
                        .profileUrl(profileUrl)
                        .point(0)
                        .social(socialType)
                        .build()
        ));
    }

    /**
     * [메서드 레벨]
     * 로그인 응답 생성
     */
    private LoginResponse buildLoginResponse(CustomMemberDetails userDetails, String token) {
        return LoginResponse.builder()
                .message("로그인 성공")
                .accessToken(token)
                .user(LoginResponse.UserInfo.builder()
                        .id(String.valueOf(userDetails.getId()))
                        .userId(userDetails.getUsername())
                        .name(userDetails.getName())
                        .profileImg(userDetails.getProfileImg())
                        .build())
                .build();
    }
}
