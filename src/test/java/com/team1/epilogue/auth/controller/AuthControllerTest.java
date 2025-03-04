package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import com.team1.epilogue.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * [클래스 레벨]
 * AuthController의 단위 테스트 클래스.
 */
@WebMvcTest(AuthController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthController 테스트")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * [테스트] 일반 로그인 성공 케이스
     */
    @Test
    @DisplayName("일반 로그인 성공 테스트")
    public void testGeneralLoginSuccess() throws Exception {
        // Given: 입력 데이터 설정
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("testUser");
        request.setPassword("testPassword");

        LoginResponse response = LoginResponse.builder()
                .accessToken("dummyToken")
                .user(LoginResponse.UserInfo.builder()
                        .userId("1")
                        .build())
                .build();

        // When: Mock 객체의 동작 정의
        when(authService.login(any(GeneralLoginRequest.class))).thenReturn(response);

        // Then: API 호출 및 검증
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummyToken"))
                .andExpect(jsonPath("$.user.userId").value("1"));
    }

    /**
     * [테스트] 일반 로그인 실패 - 잘못된 ID/비밀번호
     */
    @Test
    @DisplayName("일반 로그인 실패 테스트 - 잘못된 ID/비밀번호")
    public void testGeneralLoginFailure() throws Exception {
        // Given: 입력 데이터 설정
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("wrongUser");
        request.setPassword("wrongPassword");

        // When: Mock 객체의 동작 정의 - 로그인 실패 예외 발생
        when(authService.login(any(GeneralLoginRequest.class)))
                .thenThrow(new BadCredentialsException("아이디 혹은 패스워드가 틀립니다."));

        // Then: API 호출 및 검증
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("아이디 혹은 패스워드가 틀립니다."));
    }

    /**
     * [테스트] 소셜 로그인 성공 케이스
     */
    @Test
    @DisplayName("소셜 로그인 성공 테스트")
    public void testSocialLoginSuccess() throws Exception {
        // Given: 입력 데이터 설정
        SocialLoginRequest request = new SocialLoginRequest();
        request.setProvider("google");
        request.setAccessToken("validAccessToken");

        LoginResponse response = LoginResponse.builder()
                .accessToken("socialDummyToken")
                .user(LoginResponse.UserInfo.builder()
                        .userId("2")
                        .build())
                .build();

        // When :Mock 객체의 동작 정의
        when(authService.socialLogin(any(SocialLoginRequest.class))).thenReturn(response);

        // Then: API 호출 및 검증
        mockMvc.perform(post("/api/members/login/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("socialDummyToken"))
                .andExpect(jsonPath("$.user.userId").value("2"));
    }

    /**
     * [테스트] 소셜 로그인 실패 - 지원되지 않는 provider
     */
    @Test
    @DisplayName("소셜 로그인 실패 테스트 - 지원되지 않는 provider")
    public void testSocialLoginFailure() throws Exception {
        // Given: 입력 데이터 설정
        SocialLoginRequest request = new SocialLoginRequest();
        request.setProvider("unsupportedProvider");
        request.setAccessToken("invalidAccessToken");

        // When: Mock 객체의 동작 정의 - 예외 발생
        when(authService.socialLogin(any(SocialLoginRequest.class)))
                .thenThrow(new IllegalArgumentException("지원되지 않는 소셜 로그인 제공자입니다."));

        // Then: API 호출 및 검증
        mockMvc.perform(post("/api/members/login/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("지원되지 않는 소셜 로그인 제공자입니다."));
    }
}
