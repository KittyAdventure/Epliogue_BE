package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.CustomUserDetailsService;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import com.team1.epilogue.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)  // SecurityConfig를 테스트 컨텍스트에 포함
@WebMvcTest(MemberController.class)
@DisplayName("MemberController 테스트")
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberWithdrawalService memberWithdrawalService;

    // SecurityConfig에서 요구하는 CustomUserDetailsService 빈 모킹
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // 인증된 사용자를 위한 CustomMemberDetails
    private CustomMemberDetails memberDetails;

    @BeforeEach
    void setUp() {
        memberDetails = new CustomMemberDetails(
                1L,
                "testUser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "Test User",
                "http://example.com/profile.jpg"
        );
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void testRegisterMember() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLoginId("testUser");
        request.setPassword("password123");
        request.setNickname("testNick");
        request.setName("Test User");
        request.setBirthDate("1990-01-01");
        request.setEmail("test@example.com");
        request.setPhone("010-1111-2222");
        request.setProfileUrl("http://example.com/profile.jpg");

        MemberResponse response = MemberResponse.builder()
                .id("1")
                .loginId("testUser")
                .nickname("testNick")
                .name("Test User")
                .birthDate("1990-01-01")
                .email("test@example.com")
                .phone("010-1111-2222")
                .profileUrl("http://example.com/profile.jpg")
                .build();

        when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/members/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.loginId").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void testWithdrawMember() throws Exception {
        doNothing().when(memberWithdrawalService).withdrawMember(1L);

        mockMvc.perform(delete("/api/members")
                        .with(csrf())
                        .with(user(memberDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.메시지").value("유저의 계정이 정상적으로 삭제되었습니다."));

        verify(memberWithdrawalService, times(1)).withdrawMember(1L);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void testUpdateMember() throws Exception {
        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setNickname("newNick");
        request.setEmail("new@example.com");
        request.setPhone("010-5678-1234");
        request.setProfilePhoto("http://example.com/newProfile.jpg");

        MemberResponse response = MemberResponse.builder()
                .id("1")
                .loginId("testUser")
                .nickname("newNick")
                .name("Test User")
                .birthDate("1990-01-01")
                .email("new@example.com")
                .phone("010-5678-1234")
                .profileUrl("http://example.com/newProfile.jpg")
                .build();

        when(memberService.updateMember(anyLong(), any(UpdateMemberRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/members")
                        .with(csrf())
                        .with(user(memberDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.메시지").value("유저 정보 수정 완료"))
                .andExpect(jsonPath("$.유저.id").value("1"))
                .andExpect(jsonPath("$.유저.nickname").value("newNick"))
                .andExpect(jsonPath("$.유저.email").value("new@example.com"))
                .andExpect(jsonPath("$.유저.phone").value("010-5678-1234"))
                .andExpect(jsonPath("$.유저.profileUrl").value("http://example.com/newProfile.jpg"));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 인증되지 않은 사용자")
    void testWithdrawMember_Unauthorized() throws Exception {
        // 인증되지 않은 경우 (with(user(...)) 미사용)
        mockMvc.perform(delete("/api/members")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.메시지").value("인증되지 않은 사용자"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 인증되지 않은 사용자")
    void testUpdateMember_Unauthorized() throws Exception {
        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setNickname("failUser");
        request.setEmail("fail@example.com");
        request.setPhone("010-0000-0000");
        request.setProfilePhoto("http://example.com/fail.jpg");

        // 인증되지 않은 경우 (with(user(...)) 미사용)
        mockMvc.perform(put("/api/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.메시지").value("인증 실패"));
    }
}
