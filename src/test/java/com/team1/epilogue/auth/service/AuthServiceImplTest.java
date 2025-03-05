package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("일반 로그인: 올바른 자격 증명일 때 로그인 성공")
    public void shouldReturnLoginResponseForValidCredentials() {
        // Given
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("user1");
        request.setPassword("plainPassword");

        Member member = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("encodedPassword")
                .nickname("UserOne")
                .name("User One")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("user1@example.com")
                .phone("01012345678")
                .profileUrl("http://example.com/profile.jpg")
                .social(null)
                .build();

        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("1")).thenReturn("jwt-token");

        // When
        LoginResponse response = authServiceImpl.login(request);

        // Then
        assertNotNull(response);
        assertEquals("로그인 성공", response.getMessage());
        assertEquals("jwt-token", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals("1", response.getUser().getId());
        assertEquals("user1", response.getUser().getUserId());
    }

    @Test
    @DisplayName("일반 로그인: 비밀번호가 일치하지 않을 때 예외 발생")
    public void shouldThrowBadCredentialsExceptionWhenPasswordIsInvalid() {
        // Given
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("user1");
        request.setPassword("wrongPassword");

        Member member = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("encodedPassword")
                .build();

        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authServiceImpl.login(request));
    }

    @Test
    @DisplayName("구글 소셜 로그인: 로그인 성공 시 응답 반환")
    public void shouldReturnGoogleLoginResponseForSocialLogin() {
        // Given
        GoogleUserInfo googleUserInfo = new GoogleUserInfo();
        googleUserInfo.setSub("123456789");
        googleUserInfo.setEmail("google@example.com");
        googleUserInfo.setName("Google User");
        googleUserInfo.setPicture("http://example.com/google.jpg");

        when(googleAuthService.getGoogleUserInfo("google-access-token")).thenReturn(googleUserInfo);

        Member member = Member.builder()
                .id(2L)
                .loginId("google_123456789")
                .nickname("Google User")
                .name("Google User")
                .email("google@example.com")
                .profileUrl("http://example.com/google.jpg")
                .build();
        when(memberRepository.findByEmail("google@example.com")).thenReturn(Optional.of(member));
        when(jwtTokenProvider.generateToken("2")).thenReturn("jwt-google-token");

        // When
        LoginResponse response = authServiceImpl.socialLoginGoogle(googleUserInfo);

        // Then
        assertNotNull(response);
        assertEquals("로그인 성공", response.getMessage());
        assertEquals("jwt-google-token", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals("2", response.getUser().getId());
        assertEquals("google_123456789", response.getUser().getUserId());
    }

    @Test
    @DisplayName("카카오 소셜 로그인: 로그인 성공 시 응답 반환")
    public void shouldReturnKakaoLoginResponseForSocialLogin() {
        // Given
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        kakaoUserInfo.setId(98765L);
        KakaoUserInfo.KakaoAccount account = new KakaoUserInfo.KakaoAccount();
        account.setEmail("kakao@example.com");
        KakaoUserInfo.KakaoProfile profile = new KakaoUserInfo.KakaoProfile();
        profile.setNickname("KakaoUser");
        profile.setProfileImageUrl("http://example.com/kakao.jpg");
        account.setProfile(profile);
        kakaoUserInfo.setKakao_account(account);

        when(kakaoAuthService.getKakaoUserInfo("kakao-access-token")).thenReturn(kakaoUserInfo);

        Member member = Member.builder()
                .id(3L)
                .loginId("kakao_98765")
                .nickname("KakaoUser")
                .name("KakaoUser")
                .email("kakao@example.com")
                .profileUrl("http://example.com/kakao.jpg")
                .build();
        when(memberRepository.findByEmail("kakao@example.com")).thenReturn(Optional.of(member));
        when(jwtTokenProvider.generateToken("3")).thenReturn("jwt-kakao-token");

        // When
        LoginResponse response = authServiceImpl.socialLoginKakao(kakaoUserInfo);

        // Then
        assertNotNull(response);
        assertEquals("로그인 성공", response.getMessage());
        assertEquals("jwt-kakao-token", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals("3", response.getUser().getId());
        assertEquals("kakao_98765", response.getUser().getUserId());
    }
}
