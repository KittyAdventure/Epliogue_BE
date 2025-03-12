package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.*;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.*;
import com.team1.epilogue.auth.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private RegisterRequest registerRequest;

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setLoginId("serviceMember");
        registerRequest.setPassword("password123");
        registerRequest.setNickname("serviceNick");
        registerRequest.setName("Service Member");
        registerRequest.setBirthDate("1990-01-01");
        registerRequest.setEmail("service@example.com");
        registerRequest.setPhone("010-1234-5678");
        registerRequest.setProfileUrl("http://example.com/photo.jpg");
    }

    @Test
    @DisplayName("정상 회원 등록 테스트")
    public void testRegisterMemberSuccess() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        when(memberRepository.save(any(Member.class))).thenReturn(Member.builder()
                .id(1L)
                .loginId(registerRequest.getLoginId())
                .password("encodedPassword123")
                .nickname(registerRequest.getNickname())
                .name(registerRequest.getName())
                .birthDate(LocalDate.parse(registerRequest.getBirthDate()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .profileUrl(registerRequest.getProfileUrl())
                .point(0)
                .social(null)
                .build()
        );

        // MultipartFile이 없으므로 null 전달
        MemberResponse response = memberService.registerMember(registerRequest, null);

        assertNotNull(response);
        assertEquals("serviceMember", response.getLoginId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 로그인 ID 테스트")
    public void testDuplicateMemberId() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(true);

        assertThrows(IdAlreadyExistException.class, () -> memberService.registerMember(registerRequest, null));
    }

    @Test
    @DisplayName("중복 이메일 테스트")
    public void testDuplicateEmail() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> memberService.registerMember(registerRequest, null));
    }

    @Test
    @DisplayName("정상 회원 정보 수정 테스트")
    public void testUpdateMemberSuccess() {
        Member existingMember = Member.builder()
                .id(1L)
                .loginId("serviceMember")
                .password("encodedPassword123")
                .nickname("serviceNick")
                .name("Service Member")
                .birthDate(LocalDate.parse("1990-01-01"))
                .email("service@example.com")
                .phone("010-1234-5678")
                .profileUrl("http://example.com/photo.jpg")
                .point(0)
                .social(null)
                .build();

        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("010-0000-0000");
        updateRequest.setProfilePhoto("http://example.com/newphoto.jpg");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        MemberResponse response = memberService.updateMember(1L, updateRequest, null);

        assertNotNull(response);
        assertEquals("updatedNick", response.getNickname());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    @DisplayName("회원 정보 수정 - 회원이 존재하지 않을 경우")
    public void testUpdateMemberNotFound() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(1L, updateRequest, null));
    }
}
