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

/**
 * [클래스 레벨]
 * MemberService에 대한 단위 테스트 클래스.
 */
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

    /**
     * [설정 메서드]
     * 각 테스트 실행 전, 기본 데이터를 초기화하는 메서드.
     */
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

    /**
     * [테스트 메서드]
     * 회원 가입 성공 테스트.
     */
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

        MemberResponse response = memberService.registerMember(registerRequest);

        assertNotNull(response);
        assertEquals("serviceMember", response.getLoginId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    /**
     * [테스트 메서드]
     * 중복된 로그인 ID가 존재하는 경우 회원가입 실패 테스트.
     */
    @Test
    @DisplayName("중복 로그인 ID 테스트")
    public void testDuplicateMemberId() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(true);

        assertThrows(IdAlreadyExistException.class, () -> memberService.registerMember(registerRequest));
    }

    /**
     * [테스트 메서드]
     * 중복된 이메일이 존재하는 경우 회원가입 실패 테스트.
     */
    @Test
    @DisplayName("중복 이메일 테스트")
    public void testDuplicateEmail() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> memberService.registerMember(registerRequest));
    }

    /**
     * [테스트 메서드]
     * 회원 정보 수정 성공 테스트.
     */
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

        MemberResponse response = memberService.updateMember(1L, updateRequest);

        assertNotNull(response);
        assertEquals("updatedNick", response.getNickname());
        assertEquals("updated@example.com", response.getEmail());
    }

    /**
     * [테스트 메서드]
     * 존재하지 않는 회원을 수정하려고 할 때 실패 테스트.
     */
    @Test
    @DisplayName("회원 정보 수정 - 회원이 존재하지 않을 경우")
    public void testUpdateMemberNotFound() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(1L, updateRequest));
    }
}
