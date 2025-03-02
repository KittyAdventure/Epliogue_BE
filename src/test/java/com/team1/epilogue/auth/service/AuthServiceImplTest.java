package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthServiceImplTest 테스트")
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private KakaoAuthService kakaoAuthService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Member existingMember;
    private GeneralLoginRequest generalLoginRequest;

    @BeforeEach
    public void setUp() {
        existingMember = Member.builder()
                .id(1L)
                .loginId("testUser")
                .password("encodedPassword")
                .nickname("testNick")
                .name("Test User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .phone("010-1111-2222")
                .profileUrl("http://example.com/profile.jpg")
                .point(0)
                .social(null)
                .build();

        generalLoginRequest = new GeneralLoginRequest();
        generalLoginRequest.setLoginId("testUser");
        generalLoginRequest.setPassword("plainPassword");
    }

    @Test
    @DisplayName("일반 로그인 성공 테스트")
    public void testGeneralLoginSuccess() {
        // 회원 존재 및 비밀번호 일치
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("jwt-token");

        LoginResponse response = authService.login(generalLoginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("testUser", response.getUser().getUserId());
        verify(memberRepository, times(1)).findByLoginId("testUser");
    }

    @Test
    @DisplayName("일반 로그인 실패 - 사용자 미존재")
    public void testGeneralLoginUserNotFound() {
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(generalLoginRequest));

        verify(memberRepository, times(1)).findByLoginId("testUser");
    }

    @Test
    @DisplayName("일반 로그인 실패 - 비밀번호 불일치")
    public void testGeneralLoginPasswordMismatch() {
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(generalLoginRequest));

        verify(memberRepository, times(1)).findByLoginId("testUser");
    }

    @Test
    @DisplayName("소셜 로그인 - 구글 성공 테스트")
    public void testSocialLoginGoogleSuccess() {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setProvider("google");
        request.setAccessToken("google-token");

        // 더미 구글 사용자 정보 생성
        GoogleUserInfo googleUser = new GoogleUserInfo();
        googleUser.setSub("google123");
        googleUser.setName("Google User");
        googleUser.setEmail("google@example.com");
        googleUser.setPicture("http://example.com/google.jpg");

        when(googleAuthService.getGoogleUserInfo("google-token")).thenReturn(googleUser);
        // 이미 가입된 경우
        when(memberRepository.findByEmail("google@example.com")).thenReturn(Optional.of(existingMember));
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("jwt-google-token");

        LoginResponse response = authService.socialLogin(request);

        assertNotNull(response);
        assertEquals("jwt-google-token", response.getAccessToken());
        assertEquals("testUser", response.getUser().getUserId());
    }

    @Test
    @DisplayName("소셜 로그인 - 카카오 성공 테스트")
    public void testSocialLoginKakaoSuccess() {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setProvider("kakao");
        request.setAccessToken("kakao-token");

        // 더미 카카오 사용자 정보 생성
        KakaoUserInfo kakaoUser = new KakaoUserInfo();
        kakaoUser.setId(123456L);
        KakaoUserInfo.KakaoAccount kakaoAccount = new KakaoUserInfo.KakaoAccount();
        kakaoAccount.setEmail("kakao@example.com");
        KakaoUserInfo.KakaoProfile kakaoProfile = new KakaoUserInfo.KakaoProfile();
        kakaoProfile.setNickname("Kakao User");
        kakaoProfile.setProfileImageUrl("http://example.com/kakao.jpg");
        kakaoAccount.setProfile(kakaoProfile);
        kakaoUser.setKakao_account(kakaoAccount);

        when(kakaoAuthService.getKakaoUserInfo("kakao-token")).thenReturn(kakaoUser);
        when(memberRepository.findByEmail("kakao@example.com")).thenReturn(Optional.of(existingMember));
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("jwt-kakao-token");

        LoginResponse response = authService.socialLogin(request);

        assertNotNull(response);
        assertEquals("jwt-kakao-token", response.getAccessToken());
        assertEquals("testUser", response.getUser().getUserId());
    }
}
