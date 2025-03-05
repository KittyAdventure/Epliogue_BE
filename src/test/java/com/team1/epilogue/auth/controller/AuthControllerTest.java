package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.service.*;
import com.team1.epilogue.config.TestSecurityConfig;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * [클래스 레벨]
 * AuthController의 단위 테스트 클래스
 * - 로그인, 소셜 로그인, 소셜 회원 탈퇴 기능을 테스트
 */
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private KakaoAuthService kakaoAuthService;

    @MockitoBean
    private GoogleAuthService googleAuthService;


    @MockitoBean
    private KakaoWithdrawalService kakaoWithdrawalService;

    @MockitoBean
    private GoogleWithdrawalService googleWithdrawalService;


    @MockitoBean
    private MemberWithdrawalService memberWithdrawalService;

    @MockitoBean
    private LogoutService logoutService;
    /**
     * [메서드 레벨]
     * 일반 로그인 성공 테스트
     * - 올바른 로그인 ID와 비밀번호 입력 시, 정상적으로 JWT 토큰과 사용자 정보를 반환하는지 확인
     */
    @Test
    @DisplayName("일반 로그인 성공 테스트")
    public void testLogin_Success() throws Exception {
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("testUser");
        request.setPassword("password");

        LoginResponse loginResponse = LoginResponse.builder()
                .message("로그인 성공")
                .accessToken("jwt-token")
                .user(LoginResponse.UserInfo.builder()
                        .id("1")
                        .userId("testUser")
                        .name("Test User")
                        .profileImg("http://example.com/profile.jpg")
                        .build())
                .build();

        Mockito.when(authService.login(Mockito.any(GeneralLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/api/members/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }


    /**
     * [메서드 레벨]
     * 카카오 소셜 로그인 성공 테스트
     * - 카카오 API에서 인증 코드를 받아, 정상적으로 로그인 응답을 반환하는지 확인
     */
    @Test
    @DisplayName("카카오 소셜 로그인 콜백 성공 테스트")
    public void testSocialLogin_KakaoCallback_Success() throws Exception {
        String code = "sample-code";

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        kakaoUserInfo.setId(12345L);
        KakaoUserInfo.KakaoAccount account = new KakaoUserInfo.KakaoAccount();
        account.setEmail("kakao@example.com");
        KakaoUserInfo.KakaoProfile profile = new KakaoUserInfo.KakaoProfile();
        profile.setNickname("KakaoUser");
        profile.setProfileImageUrl("http://example.com/kakao.jpg");
        account.setProfile(profile);
        kakaoUserInfo.setKakao_account(account);

        LoginResponse loginResponse = LoginResponse.builder()
                .message("로그인 성공")
                .accessToken("jwt-token")
                .user(LoginResponse.UserInfo.builder()
                        .id("1")
                        .userId("kakao_12345")
                        .name("KakaoUser")
                        .profileImg("http://example.com/kakao.jpg")
                        .build())
                .build();

        Mockito.when(kakaoAuthService.getKakaoUserInfo(code)).thenReturn(kakaoUserInfo);
        Mockito.when(authService.socialLoginKakao(Mockito.any(KakaoUserInfo.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(get("/api/members/auth/kakao/callback")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }



    // 카카오 소셜 회원 탈퇴 성공 테스트
    @Test
    @DisplayName("카카오 소셜 회원 탈퇴 성공 테스트")
    public void testWithdrawSocialMember_Kakao_Success() throws Exception {
        // 인증된 사용자 생성
        CustomMemberDetails customMemberDetails = CustomMemberDetails.fromMember(
                Member.builder()
                        .id(1L)
                        .loginId("kakao_12345")
                        .password("")
                        .name("KakaoUser")
                        .profileUrl("http://example.com/kakao.jpg")
                        .build()
        );

        mockMvc.perform(delete("/api/members/social/withdraw")
                        .with(csrf())
                        .param("provider", "kakao")
                        .param("accessToken", "sample-access-token")
                        .with(user(customMemberDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("소셜 회원 탈퇴 성공"));
    }

    /**
     * [메서드 레벨]
     * 구글 소셜 회원 탈퇴 성공 테스트
     * - 인증된 사용자가 구글 연동을 해제하고, DB에서 정상적으로 삭제되는지 확인
     */
    // 구글 소셜 회원 탈퇴 성공 테스트
    @Test
    @DisplayName("구글 소셜 회원 탈퇴 성공 테스트")
    public void testWithdrawSocialMember_Google_Success() throws Exception {
        // 인증된 사용자 생성
        CustomMemberDetails customMemberDetails = CustomMemberDetails.fromMember(
                Member.builder()
                        .id(1L)
                        .loginId("testUser")
                        .password("")
                        .name("Test User")
                        .profileUrl("http://example.com/profile.jpg")
                        .build()
        );
        mockMvc.perform(delete("/api/members/social/withdraw")
                        .with(csrf())
                        .param("provider", "google")
                        .param("accessToken", "sample-google-access-token")
                        .with(user(customMemberDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("소셜 회원 탈퇴 성공"));
    }

    // 테스트용 인증 principal 생성 (로그아웃 테스트용)
    private CustomMemberDetails testUser() {
        return CustomMemberDetails.fromMember(
                Member.builder()
                        .id(1L)
                        .loginId("testUser")
                        .password("encodedPassword")
                        .name("Test User")
                        .profileUrl("http://example.com/profile.jpg")
                        .build()
        );
    }

    // 일반 로그아웃 성공 테스트
    @Test
    @DisplayName("일반 로그아웃 성공 테스트")
    public void testLogout_Success() throws Exception {
        // 인증된 사용자 시뮬레이션
        CustomMemberDetails user = testUser();

        mockMvc.perform(post("/api/members/logout")
                        .with(csrf())
                        .with(user(user))
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 성공"));
    }
    @Test
    @DisplayName("소셜 로그아웃 성공 테스트")
    public void 소셜_로그아웃_성공_테스트() throws Exception {
        CustomMemberDetails customMemberDetails = CustomMemberDetails.fromMember(
                Member.builder()
                        .id(1L)
                        .loginId("testUser")
                        .password("")
                        .name("Test User")
                        .profileUrl("http://example.com/profile.jpg")
                        .build()
        );
        mockMvc.perform(post("/api/members/logout")
                        .with(csrf())
                        .with(user(customMemberDetails))
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 성공"));

    }
}