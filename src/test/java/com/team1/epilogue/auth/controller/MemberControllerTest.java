package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)  // WebMvc 환경에서 테스트 (Spring Context 부담 감소)
@MockitoSettings(strictness = Strictness.LENIENT) // Mockito 경고 억제
@DisplayName("MemberController 테스트")
public class MemberControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberService memberService;

    @Mock
    private MemberWithdrawalService memberWithdrawalService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberController memberController;

    public MemberControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void testRegisterMemberSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLoginId("newUser");
        request.setPassword("password123");
        request.setNickname("newNick");
        request.setName("New User");
        request.setBirthDate("1990-01-01");
        request.setEmail("new@example.com");
        request.setPhone("010-1234-5678");
        request.setProfileUrl("http://example.com/profile.jpg");

        MemberResponse response = MemberResponse.builder()
                .id("1")
                .loginId("newUser")
                .nickname("newNick")
                .name("New User")
                .birthDate("1990-01-01")
                .email("new@example.com")
                .phone("010-1234-5678")
                .profileUrl("http://example.com/profile.jpg")
                .build();

        when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())  // 최신 방식: 201 Created 사용
                .andExpect(jsonPath("$.loginId").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("회원정보 수정 성공 테스트")
    public void testUpdateMemberSuccess() throws Exception {
        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setNickname("updatedNick");
        request.setEmail("updated@example.com");
        request.setPhone("010-9876-5432");
        request.setProfilePhoto("http://example.com/updated.jpg");

        MemberResponse response = MemberResponse.builder()
                .id("1")
                .loginId("existingUser")
                .nickname("updatedNick")
                .name("Existing User")
                .birthDate("1990-01-01")
                .email("updated@example.com")
                .phone("010-9876-5432")
                .profileUrl("http://example.com/updated.jpg")
                .build();

        when(memberService.updateMember(any(Long.class), any(UpdateMemberRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/members")
                        .header("Authorization", "Bearer mock-token")  // JWT 인증 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("유저 정보 수정완료"))
                .andExpect(jsonPath("$.user.nickname").value("updatedNick"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    public void testWithdrawMemberSuccess() throws Exception {
        doNothing().when(memberWithdrawalService).withdrawMember(1L);

        mockMvc.perform(delete("/api/members")
                        .header("Authorization", "Bearer mock-token"))  // JWT 인증 추가
                .andExpect(status().isNoContent());
    }
}
