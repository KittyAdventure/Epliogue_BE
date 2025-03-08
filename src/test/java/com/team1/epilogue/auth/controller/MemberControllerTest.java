package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.ApiResponse;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import com.team1.epilogue.config.TestSecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@DisplayName("MemberController 전체 테스트")
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MemberWithdrawalService memberWithdrawalService;

    // 인증된 사용자 생성 헬퍼 메서드
    private CustomMemberDetails getAuthenticatedUser() {
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("password")
                .nickname("user1")
                .name("User One")
                .email("user1@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.png")
                // 만약 birthDate 필드가 있다면 예: .birthDate("2000-01-01")
                .build();
        return CustomMemberDetails.fromMember(dummyMember);
    }

    @BeforeEach
    public void setUpSecurityContext() {
        // 필요한 테스트에서 인증이 필요한 경우 setUpSecurityContext() 호출
        CustomMemberDetails customMemberDetails = getAuthenticatedUser();
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("회원가입 API")
    class RegisterMemberTests {
        @Test
        @DisplayName("회원가입 성공")
        public void testRegisterMemberSuccess() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setLoginId("user1");
            request.setPassword("password");
            request.setName("User One");
            request.setEmail("user1@example.com");
            request.setNickname("user1");
            request.setBirthDate("2000-01-01");
            request.setPhone("010-1111-1111");
            request.setProfileUrl("http://example.com/profile.png");

            MemberResponse memberResponse = MemberResponse.builder()
                    .id("1")
                    .loginId("user1")
                    .nickname("user1")
                    .name("User One")
                    .birthDate("2000-01-01")
                    .email("user1@example.com")
                    .phone("010-1111-1111")
                    .profileUrl("http://example.com/profile.png")
                    .build();

            when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(memberResponse);

            mockMvc.perform(post("/api/members/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.data.loginId", is("user1")))
                    .andExpect(jsonPath("$.data.name", is("User One")))
                    .andExpect(jsonPath("$.data.email", is("user1@example.com")));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 API")
    class WithdrawMemberTests {
        @Test
        @DisplayName("회원 탈퇴 성공 (인증된 사용자)")
        public void testWithdrawMemberSuccess() throws Exception {
            CustomMemberDetails customMemberDetails = getAuthenticatedUser();
            // 이미 setUpSecurityContext()에서 인증 정보가 설정됨
            doNothing().when(memberWithdrawalService).withdrawMember(customMemberDetails.getId());

            mockMvc.perform(delete("/api/members")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.data.message", is("User account deleted successfully ")));
        }

        @Test
        @DisplayName("회원 탈퇴 실패 (인증 미비)")
        public void testWithdrawMemberUnauthorized() throws Exception {
            // 인증 정보를 지우고 호출
            SecurityContextHolder.clearContext();
            mockMvc.perform(delete("/api/members")
                            .with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.error", is("Unauthorized user")));
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 API")
    class UpdateMemberTests {
        @Test
        @DisplayName("회원 정보 수정 성공 (인증된 사용자)")
        public void testUpdateMemberSuccess() throws Exception {
            UpdateMemberRequest updateRequest = new UpdateMemberRequest();
            updateRequest.setNickname("updatedNick");
            updateRequest.setEmail("updated@example.com");
            updateRequest.setPhone("010-2222-3333");
            updateRequest.setProfilePhoto("http://example.com/updated.png");

            MemberResponse updatedResponse = MemberResponse.builder()
                    .id("1")
                    .loginId("user1")
                    .nickname("updatedNick")
                    .name("User One")
                    .birthDate("2000-01-01")
                    .email("updated@example.com")
                    .phone("010-2222-3333")
                    .profileUrl("http://example.com/updated.png")
                    .build();

            when(memberService.updateMember(any(Long.class), any(UpdateMemberRequest.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/api/members")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.data.nickname", is("updatedNick")))
                    .andExpect(jsonPath("$.data.email", is("updated@example.com")))
                    .andExpect(jsonPath("$.data.phone", is("010-2222-3333")))
                    .andExpect(jsonPath("$.data.profileUrl", is("http://example.com/updated.png")));
        }

        @Test
        @DisplayName("회원 정보 수정 실패 (인증 미비)")
        public void testUpdateMemberUnauthorized() throws Exception {
            UpdateMemberRequest updateRequest = new UpdateMemberRequest();
            updateRequest.setNickname("updatedNick");
            updateRequest.setEmail("updated@example.com");
            updateRequest.setPhone("010-2222-3333");
            updateRequest.setProfilePhoto("http://example.com/updated.png");
            Authentication fakeAuth = mock(Authentication.class);
            when(fakeAuth.isAuthenticated()).thenReturn(false);
            when(fakeAuth.getPrincipal()).thenReturn("anonymous");

            // 가짜 인증 정보를 SecurityContextHolder에 설정
            SecurityContextHolder.getContext().setAuthentication(fakeAuth);
            mockMvc.perform(put("/api/members")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.error", is("Unauthorized")));
        }
    }
}
