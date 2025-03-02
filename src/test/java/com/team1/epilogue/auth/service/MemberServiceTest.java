package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.dto.UpdateMemberRequest;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.IdAlreadyExistException;
import com.team1.epilogue.auth.exception.EmailAlreadyExistException;
import com.team1.epilogue.auth.exception.EmailNotValidException;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
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

        Member memberToSave = Member.builder()
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
                .build();

        Member savedMember = Member.builder()
                .id(1L)
                .loginId(memberToSave.getLoginId())
                .password(memberToSave.getPassword())
                .nickname(memberToSave.getNickname())
                .name(memberToSave.getName())
                .birthDate(memberToSave.getBirthDate())
                .email(memberToSave.getEmail())
                .phone(memberToSave.getPhone())
                .profileUrl(memberToSave.getProfileUrl())
                .point(memberToSave.getPoint())
                .social(memberToSave.getSocial())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        MemberResponse response = memberService.registerMember(registerRequest);

        assertNotNull(response);
        assertEquals("serviceMember", response.getLoginId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 로그인 ID 테스트")
    public void testDuplicateMemberId() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(true);

        IdAlreadyExistException ex = assertThrows(IdAlreadyExistException.class, () -> {
            memberService.registerMember(registerRequest);
        });
        assertEquals("이미 등록된 사용자 ID입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("중복 이메일 테스트")
    public void testDuplicateEmail() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(true);

        EmailAlreadyExistException ex = assertThrows(EmailAlreadyExistException.class, () -> {
            memberService.registerMember(registerRequest);
        });
        assertEquals("이미 등록된 이메일입니다.", ex.getMessage());
    }


    @Test
    @DisplayName("정상 회원 정보 수정 테스트")
    public void testUpdateMemberSuccess() {
        // 기존 회원 정보 생성 (등록된 상태)
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

        // 업데이트 요청 DTO 생성
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("010-0000-0000");
        updateRequest.setProfilePhoto("http://example.com/newphoto.jpg");

        // 기존 회원 조회 모의 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));

        // 업데이트 후 저장될 회원 정보 모의 설정
        Member updatedMember = Member.builder()
                .id(existingMember.getId())
                .loginId(existingMember.getLoginId())
                .password(existingMember.getPassword())
                .nickname(updateRequest.getNickname())
                .name(existingMember.getName())
                .birthDate(existingMember.getBirthDate())
                .email(updateRequest.getEmail())
                .phone(updateRequest.getPhone())
                .profileUrl(updateRequest.getProfilePhoto())
                .point(existingMember.getPoint())
                .social(existingMember.getSocial())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        MemberResponse response = memberService.updateMember(1L, updateRequest);

        assertNotNull(response);
        assertEquals("updatedNick", response.getNickname());
        assertEquals("updated@example.com", response.getEmail());
        assertEquals("010-0000-0000", response.getPhone());
        assertEquals("http://example.com/newphoto.jpg", response.getProfileUrl());
    }

    @Test
    @DisplayName("회원 정보 수정 - 회원이 존재하지 않을 경우")
    public void testUpdateMemberNotFound() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("010-0000-0000");
        updateRequest.setProfilePhoto("http://example.com/newphoto.jpg");

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        MemberNotFoundException ex = assertThrows(MemberNotFoundException.class, () -> {
            memberService.updateMember(1L, updateRequest);
        });
        assertEquals("회원이 존재하지 않습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("회원 정보 수정 - 잘못된 이메일 형식 테스트")
    public void testUpdateMemberInvalidEmail() {
        // 기존 회원 정보 생성
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
        updateRequest.setEmail("invalid-email");  // 올바르지 않은 이메일 형식
        updateRequest.setPhone("010-0000-0000");
        updateRequest.setProfilePhoto("http://example.com/newphoto.jpg");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));

        assertThrows(EmailNotValidException.class, () -> {
            memberService.updateMember(1L, updateRequest);
        });
    }
}
