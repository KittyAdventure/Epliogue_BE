package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * [클래스 레벨]
 * MemberWithdrawalService에 대한 단위 테스트 클래스.
 */
@DisplayName("MemberWithdrawalService 테스트")
public class MemberWithdrawalServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberWithdrawalService memberWithdrawalService;

    /**
     * [설정 메서드]
     * 각 테스트 실행 전, 기본 데이터를 초기화하는 메서드.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * [테스트 메서드]
     * 회원 탈퇴 성공 테스트.
     */
    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    public void testWithdrawMemberSuccess() {
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        memberWithdrawalService.withdrawMember(memberId);

        verify(memberRepository, times(1)).delete(member);
    }

    /**
     * [테스트 메서드]
     * 존재하지 않는 회원을 탈퇴하려고 할 때 실패 테스트.
     */
    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 존재하지 않는 회원")
    public void testWithdrawMemberNotFound() {
        Long memberId = 2L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                memberWithdrawalService.withdrawMember(memberId)
        );

        assertEquals("회원 정보를 찾을 수 없습니다.", exception.getMessage());
    }
}
