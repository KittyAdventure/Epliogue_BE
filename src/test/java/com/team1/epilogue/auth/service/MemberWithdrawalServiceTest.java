package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.entity.Member;
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

@DisplayName("MemberWithdrawalService 테스트")
public class MemberWithdrawalServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberWithdrawalService memberWithdrawalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
