package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements com.team1.epilogue.auth.service.AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final com.team1.epilogue.auth.service.GoogleAuthService googleAuthService;
    private final com.team1.epilogue.auth.service.KakaoAuthService kakaoAuthService;

    @Override
    public LoginResponse login(GeneralLoginRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("Invalid username or password.");
        }
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    @Override
    public LoginResponse socialLogin(SocialLoginRequest request) {
        Member member;
        if ("google".equalsIgnoreCase(request.getProvider())) {
            GoogleUserInfo googleUser = googleAuthService.getGoogleUserInfo(request.getAccessToken());
            String unifiedLoginId = "google_" + googleUser.getSub();
            member = findOrCreateMember(
                    googleUser.getEmail(),
                    unifiedLoginId,
                    googleUser.getName(),
                    googleUser.getPicture(),
                    "google"
            );
        } else if ("kakao".equalsIgnoreCase(request.getProvider())) {
            KakaoUserInfo kakaoUser = kakaoAuthService.getKakaoUserInfo(request.getAccessToken());
            String unifiedLoginId = "kakao_" + kakaoUser.getId();
            member = findOrCreateMember(
                    kakaoUser.getKakao_account().getEmail(),
                    unifiedLoginId,
                    kakaoUser.getKakao_account().getProfile().getNickname(),
                    kakaoUser.getKakao_account().getProfile().getProfileImageUrl(),
                    "kakao"
            );
        } else {
            throw new IllegalArgumentException("Unsupported social provider.");
        }
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    @Override
    public LoginResponse socialLoginKakao(KakaoUserInfo kakaoUserInfo) {
        Member member = findOrCreateMember(
                kakaoUserInfo.getKakao_account().getEmail(),
                "kakao_" + kakaoUserInfo.getId(),
                kakaoUserInfo.getKakao_account().getProfile().getNickname(),
                kakaoUserInfo.getKakao_account().getProfile().getProfileImageUrl(),
                "kakao"
        );
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    @Override
    public LoginResponse socialLoginGoogle(GoogleUserInfo googleUserInfo) {
        String unifiedLoginId = "google_" + googleUserInfo.getSub();
        Member member = findOrCreateMember(
                googleUserInfo.getEmail(),
                unifiedLoginId,
                googleUserInfo.getName(),
                googleUserInfo.getPicture(),
                "google"
        );
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(member);
        String token = jwtTokenProvider.generateToken(String.valueOf(member.getId()));
        return buildLoginResponse(userDetails, token);
    }

    private Member findOrCreateMember(String email, String loginId, String name, String profileUrl, String socialType) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .loginId(loginId)
                                .password("") // Not used for social login
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

    private LoginResponse buildLoginResponse(CustomMemberDetails userDetails, String token) {
        return LoginResponse.builder()
                .message("Login success")
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
