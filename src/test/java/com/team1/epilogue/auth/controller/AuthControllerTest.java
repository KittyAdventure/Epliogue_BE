package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.GeneralLoginRequest;
import com.team1.epilogue.auth.dto.LoginResponse;
import com.team1.epilogue.auth.dto.SocialLoginRequest;
import com.team1.epilogue.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Mockito 설정
@DisplayName("AuthController 테스트")
public class AuthControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    public AuthControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("일반 로그인 요청 성공 테스트")
    public void testGeneralLogin() throws Exception {
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("testUser");
        request.setPassword("testPassword");

        LoginResponse response = LoginResponse.builder()
                .accessToken("dummyToken")
                .user(LoginResponse.UserInfo.builder()
                        .userId("1")
                        .build())
                .build();

        when(authService.login(any(GeneralLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummyToken"))
                .andExpect(jsonPath("$.user.userId").value("1"));
    }

    @Test
    @DisplayName("일반 로그인 실패 - 잘못된 아이디/패스워드")
    public void testGeneralLoginFailure() throws Exception {
        GeneralLoginRequest request = new GeneralLoginRequest();
        request.setLoginId("wrongUser");
        request.setPassword("wrongPassword");

        when(authService.login(any(GeneralLoginRequest.class)))
                .thenThrow(new BadCredentialsException("아이디 혹은 패스워드가 틀립니다."));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("아이디 혹은 패스워드가 틀립니다."));
    }
}
