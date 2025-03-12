package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.SuccessResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import com.team1.epilogue.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(MemberController.class)
@DisplayName("MemberController 테스트")
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MemberWithdrawalService memberWithdrawalService;

    private CustomMemberDetails memberDetails;

    @BeforeEach
    void setUp() {
        Member dummyMember = Member.builder()
                .id(1L)
                .loginId("testUser")
                .password("password")
                .nickname("user1")
                .name("Test User")
                .email("test@example.com")
                .phone("010-1111-1111")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        memberDetails = new CustomMemberDetails(
                dummyMember,
                dummyMember.getId(),
                dummyMember.getLoginId(),
                dummyMember.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                dummyMember.getName(),
                dummyMember.getProfileUrl()
        );
    }

    /**
     * 회원 가입 성공 테스트 (Multipart 요청)
     */
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

        when(memberService.registerMember(any(RegisterRequest.class), any())).thenReturn(response);

        // JSON 데이터 파트를 생성
        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        // 이미지 파일 파트 (선택적, 없으면 null 전송 가능)
        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/members/register")
                        .file(dataPart)
                        .file(imagePart)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.loginId").value("testUser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    /**
     * 회원 탈퇴 성공 테스트 (인증된 사용자)
     */
    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void testWithdrawMember() throws Exception {
        doNothing().when(memberWithdrawalService).withdrawMember(1L);

        mockMvc.perform(delete("/api/members")
                        .with(csrf())
                        .with(user(memberDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("User account deleted successfully"));

        verify(memberWithdrawalService, times(1)).withdrawMember(1L);
    }

    /**
     * 회원 정보 수정 성공 테스트 (인증된 사용자, Multipart 요청)
     */
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

        when(memberService.updateMember(anyLong(), any(UpdateMemberRequest.class), any())).thenReturn(response);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "newProfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "new dummy image content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/members")
                        .file(dataPart)
                        .file(imagePart)
                        .with(csrf())
                        .with(user(memberDetails))
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.nickname").value("newNick"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"))
                .andExpect(jsonPath("$.data.phone").value("010-5678-1234"))
                .andExpect(jsonPath("$.data.profileUrl").value("http://example.com/newProfile.jpg"));
    }

    /**
     * 회원 탈퇴 실패 테스트 - 인증되지 않은 사용자
     */
    @Test
    @DisplayName("회원 탈퇴 실패 - 인증되지 않은 사용자")
    void testWithdrawMember_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/members")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized user"));
    }

    /**
     * 회원 정보 수정 실패 테스트 - 인증되지 않은 사용자
     */
    @Test
    @DisplayName("회원 정보 수정 실패 - 인증되지 않은 사용자")
    void testUpdateMember_Unauthorized() throws Exception {
        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setNickname("failUser");
        request.setEmail("fail@example.com");
        request.setPhone("010-0000-0000");
        request.setProfilePhoto("http://example.com/fail.jpg");

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/members")
                        .file(dataPart)
                        .with(csrf())
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }
}
