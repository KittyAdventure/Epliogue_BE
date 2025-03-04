package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.config.TestSecurityConfig;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.AuthService;
import com.team1.epilogue.auth.service.GoogleWithdrawalService;
import com.team1.epilogue.auth.service.KakaoAuthService;
import com.team1.epilogue.auth.service.KakaoWithdrawalService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
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
    private KakaoWithdrawalService kakaoWithdrawalService;

    @MockitoBean
    private GoogleWithdrawalService googleWithdrawalService;

    @MockitoBean
    private MemberWithdrawalService memberWithdrawalService;

    @Test
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

        Mockito.when(authService.login(Mockito.any(GeneralLoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/members/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    public void testSocialLogin_KakaoCallback_Success() throws Exception {
        String code = "sample-code";

        // 구성: KakaoUserInfo에 필요한 값 세팅
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
        Mockito.when(authService.socialLoginKakao(Mockito.any(KakaoUserInfo.class))).thenReturn(loginResponse);

        // GET 요청은 보통 CSRF 토큰이 필요 없으므로 추가하지 않아도 됨
        mockMvc.perform(get("/api/members/auth/kakao/callback")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    public void testWithdrawSocialMember_Kakao_Success() throws Exception {
        // Mock 인증 principal 생성
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
                        .with(user(customMemberDetails))) // 인증된 사용자로 설정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.메시지").value("소셜 회원 탈퇴 성공"));
    }

    @Test
    public void testWithdrawSocialMember_Google_Success() throws Exception {
        // Mock 인증 principal 생성
        CustomMemberDetails customMemberDetails = CustomMemberDetails.fromMember(
                Member.builder()
                        .id(1L)
                        .loginId("testUser")
                        .password("")
                        .name("Test User")
                        .profileUrl("http://example.com/profile.jpg")
                        .build()
        );

// TestingAuthenticationToken 대신 with(user(...)) 사용
        mockMvc.perform(delete("/api/members/social/withdraw")
                        .with(csrf())
                        .param("provider", "google")
                        .param("accessToken", "sample-google-access-token")
                        .with(user(customMemberDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.메시지").value("소셜 회원 탈퇴 성공"));

    }
}