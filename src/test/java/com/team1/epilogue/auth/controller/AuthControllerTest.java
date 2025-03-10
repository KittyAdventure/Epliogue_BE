package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.ApiResponse;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.GoogleUserInfo;
import com.team1.epilogue.auth.dto.KakaoUserInfo;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.LoginResponse.UserInfo;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.AuthService;
import com.team1.epilogue.auth.service.GoogleAuthService;
import com.team1.epilogue.auth.service.KakaoAuthService;
import com.team1.epilogue.auth.service.GoogleWithdrawalService;
import com.team1.epilogue.auth.service.KakaoWithdrawalService;
import com.team1.epilogue.auth.service.LogoutService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import com.team1.epilogue.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController 테스트")
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
    private MemberWithdrawalService memberWithdrawalService;
    @MockitoBean
    private GoogleWithdrawalService googleWithdrawalService;
    @MockitoBean
    private KakaoWithdrawalService kakaoWithdrawalService;
    @MockitoBean
    private LogoutService logoutService;

    @Test
    @DisplayName("로그인 성공")
    public void testLoginSuccess() throws Exception {
        GeneralLoginRequest request = new GeneralLoginRequest("user1", "password");
        UserInfo userInfo = UserInfo.builder()
                .id("1")
                .userId("user1")
                .name("user1")
                .profileImg("http://example.com/image.png")
                .build();
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login success")
                .accessToken("dummyToken")
                .user(userInfo)
                .build();

        when(authService.login(any(GeneralLoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/members/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Login success")))
                .andExpect(jsonPath("$.data.accessToken", is("dummyToken")))
                .andExpect(jsonPath("$.data.user.userId", is("user1")));
    }

    @Test
    @DisplayName("로그인 실패")
    public void testLoginFailure() throws Exception {
        GeneralLoginRequest request = new GeneralLoginRequest("user1", "wrongPassword");
        when(authService.login(any(GeneralLoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password."));

        mockMvc.perform(post("/api/members/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("Invalid username or password.")));
    }

    @Test
    @DisplayName("구글 콜백 성공")
    public void testGoogleCallbackSuccess() throws Exception {
        String code = "dummyCode";
        GoogleUserInfo googleUserInfo = new GoogleUserInfo();
        googleUserInfo.setSub("12345");
        googleUserInfo.setEmail("test@example.com");
        googleUserInfo.setName("user1");
        googleUserInfo.setPicture("http://example.com/image.png");

        UserInfo userInfo = UserInfo.builder()
                .id("1")
                .userId("google_12345")
                .name("user1")
                .profileImg("http://example.com/image.png")
                .build();
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login success")
                .accessToken("googleToken")
                .user(userInfo)
                .build();

        when(googleAuthService.getGoogleUserInfo(code)).thenReturn(googleUserInfo);
        when(authService.socialLoginGoogle(googleUserInfo)).thenReturn(loginResponse);

        mockMvc.perform(get("/api/members/auth/google/callback")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Login success")))
                .andExpect(jsonPath("$.data.accessToken", is("googleToken")));
    }

    @Test
    @DisplayName("카카오 콜백 성공")
    public void testKakaoCallbackSuccess() throws Exception {
        String code = "dummyKakaoCode";
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        kakaoUserInfo.setId(100L);
        KakaoUserInfo.KakaoProfile profile = new KakaoUserInfo.KakaoProfile();
        profile.setNickname("user1");
        profile.setProfileImageUrl("http://example.com/kakao.png");
        KakaoUserInfo.KakaoAccount account = new KakaoUserInfo.KakaoAccount();
        account.setEmail("kakao@example.com");
        account.setProfile(profile);
        kakaoUserInfo.setKakao_account(account);

        UserInfo userInfo = UserInfo.builder()
                .id("1")
                .userId("kakao_100")
                .name("user1")
                .profileImg("http://example.com/kakao.png")
                .build();
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login success")
                .accessToken("kakaoToken")
                .user(userInfo)
                .build();

        when(kakaoAuthService.getKakaoUserInfo(code)).thenReturn(kakaoUserInfo);
        when(authService.socialLoginKakao(kakaoUserInfo)).thenReturn(loginResponse);

        mockMvc.perform(get("/api/members/auth/kakao/callback")
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Login success")))
                .andExpect(jsonPath("$.data.accessToken", is("kakaoToken")));
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void testLogoutSuccess() throws Exception {
        String token = "dummyToken";
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("dummyPassword")
                .nickname("user1")
                .name("user1")
                .email("user1@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.png")
                .point(0)
                .social("local")
                .build();
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(dummyMember);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.doNothing().when(logoutService).invalidate(token);

        mockMvc.perform(post("/api/members/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Logout Success")));
    }

    @Test
    @DisplayName("소셜 로그아웃 성공")
    public void testSocialLogoutSuccess() throws Exception {
        String token = "dummyToken";
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("dummyPassword")
                .nickname("user1")
                .name("user1")
                .email("user1@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.png")
                .build();
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(dummyMember);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.doNothing().when(logoutService).invalidate(token);

        mockMvc.perform(post("/api/members/logout/social")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Social Logout Success")));
    }


    @Test
    @DisplayName("소셜 회원 탈퇴 성공")
    public void testWithdrawSocialMemberSuccess() throws Exception {
        String provider = "google";
        String accessToken = "dummyAccessToken";
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("dummyPassword")
                .nickname("user1")
                .name("user1")
                .email("user1@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.png")
                .point(0)
                .social("google")
                .build();
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(dummyMember);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.doNothing().when(googleWithdrawalService).revokeGoogleAccount(accessToken);
        Mockito.doNothing().when(memberWithdrawalService).withdrawMember(dummyMember.getId());

        mockMvc.perform(delete("/api/members/social/withdraw")
                        .with(csrf())
                        .param("provider", provider)
                        .param("accessToken", accessToken)
                        .with(request -> {
                            request.setUserPrincipal(auth);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", is("Social member withdrawal success")));
    }

    @Test
    @DisplayName("소셜 회원 탈퇴 실패 - 지원되지 않는 제공자")
    public void testWithdrawSocialMemberUnsupportedProvider() throws Exception {
        String provider = "facebook";
        String accessToken = "dummyAccessToken";
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("dummyPassword")
                .nickname("user1")
                .name("user1")
                .email("user1@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.png")
                .point(0)
                .social("local")
                .build();
        CustomMemberDetails userDetails = CustomMemberDetails.fromMember(dummyMember);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(delete("/api/members/social/withdraw")
                        .with(csrf())
                        .param("provider", provider)
                        .param("accessToken", accessToken)
                        .with(request -> {
                            request.setUserPrincipal(auth);
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", is("Unsupported social provider")));
    }
}
