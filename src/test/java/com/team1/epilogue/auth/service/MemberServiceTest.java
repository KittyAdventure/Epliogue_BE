package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.exception.IdAlreadyExistException;
import com.team1.epilogue.auth.exception.EmailAlreadyExistException;
import com.team1.epilogue.auth.entity.Member;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * [클래스 레벨]
 * MemberServiceTest는 MemberService의 회원 가입 기능을 검증하는 Mockito 기반 단위 테스트 클래스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {

    /**
     * [필드 레벨]
     * 회원 정보를 저장하고 조회하는 Repository를 Mocking
     */
    @Mock
    private MemberRepository memberRepository;

    /**
     * [필드 레벨]
     * 사용자의 비밀번호를 암호화하는 컴포넌트를 Mocking
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * [필드 레벨]
     * 테스트 대상인 MemberService
     */
    @InjectMocks
    private MemberService memberService;

    /**
     * [필드 레벨]
     * 각 테스트 케이스에서 사용할 회원 가입 요청 데이터를 담은 DTO
     */
    private RegisterRequest registerRequest;

    /**
     * [메서드 레벨]
     * setup 메서드는 각 테스트 실행 전에 registerRequest 객체를 초기화
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
     * [메서드 레벨]
     * testRegisterMemberSuccess 메서드는 정상적인 회원 가입이 처리되어
     * 저장된 회원 정보와 암호화된 비밀번호가 올바른지 검증
     */
    @Test
    @DisplayName("정상 회원 등록 테스트")
    public void testRegisterMemberSuccess() {
        /* [검증 단계]
         * 로그인 ID와 이메일이 존재하지 않는 경우로 모의 설정
         */
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(false);

        /* [검증 단계]
         * 비밀번호 암호화 결과를 모의 설정
         */
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        /* [검증 단계]
         * memberRepository.save() 호출 시 반환할 Member 객체 생성.
         * (테스트에서는 ID 값을 1L로 가정)
         */
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

        /* [실행 단계]
         * MemberService의 registerMember 메서드 호출
         */
        MemberResponse response = memberService.registerMember(registerRequest);

        /* [검증 단계]
         * 응답 객체가 null이 아니며, loginId가 올바른지 확인
         */
        assertNotNull(response);
        assertEquals("serviceMember", response.getLoginId());

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    /**
     * [메서드 레벨]
     * testDuplicateMemberId 메서드는 이미 등록된 로그인 ID로 회원 가입 시,
     * IdAlreadyExistException이 발생하는지 검증
     */
    @Test
    @DisplayName("중복 로그인 ID 테스트")
    public void testDuplicateMemberId() {
        /* [검증 단계]
         * 존재 여부 조회 시 이미 존재함을 모의 설정
         */
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(true);

        /* [실행 및 검증 단계]
         * IdAlreadyExistException이 발생하는지 확인
         */
        IdAlreadyExistException ex = assertThrows(IdAlreadyExistException.class, () -> {
            memberService.registerMember(registerRequest);
        });
        assertEquals("이미 등록된 사용자 ID입니다.", ex.getMessage());
    }

    /**
     * [메서드 레벨]
     * testDuplicateEmail 메서드는 이미 등록된 이메일로 회원 가입 시,
     * EmailAlreadyExistException이 발생하는지 검증
     */
    @Test
    @DisplayName("중복 이메일 테스트")
    public void testDuplicateEmail() {
        /* [검증 단계]
         * 로그인 ID는 존재하지 않고, 이메일은 이미 존재함을 모의 설정
         */
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(true);

        /* [실행 및 검증 단계]
         * EmailAlreadyExistException이 발생하는지 확인
         */
        EmailAlreadyExistException ex = assertThrows(EmailAlreadyExistException.class, () -> {
            memberService.registerMember(registerRequest);
        });
        assertEquals("이미 등록된 이메일입니다.", ex.getMessage());
    }
}
