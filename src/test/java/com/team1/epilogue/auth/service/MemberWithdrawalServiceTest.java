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

/**
 * [클래스 레벨]
 * 회원 탈퇴 로직을 처리하는 서비스 클래스를 테스트하는 클래스
 * 이 클래스는 회원 탈퇴 로직의 정상 동작 및 예외 처리가 제대로 동작하는지 검증
 */
@DisplayName("MemberWithdrawalService 테스트")
public class MemberWithdrawalServiceTest {

    /**
     * [필드 레벨]
     * 회원 정보를 관리하는 레포지토리
     * Mock 어노테이션을 통해 MemberRepository의 동작을 모의(Mock) 처리
     */
    @Mock
    private MemberRepository memberRepository;

    /**
     * [필드 레벨]
     * 실제 회원 탈퇴 로직을 처리하는 서비스 클래스
     * InjectMocks 어노테이션을 통해 mock 객체가 주입
     */
    @InjectMocks
    private MemberWithdrawalService memberWithdrawalService;

    /**
     * [메서드 레벨]
     * 각 테스트 메서드 실행 전에 호출되는 메서드
     * MockitoAnnotations.openMocks(this) 호출을 통해 @Mock, @InjectMocks를 설정
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * [메서드 레벨]
     * 회원이 존재하는 경우 회원 탈퇴 성공 테스트
     * - 회원 정보를 정상적으로 찾아서 삭제하는지 확인
     * - 해당 회원이 삭제되는지 검증하기 위해 verify를 사용하여 delete 호출을 확인
     */
    @Test
    @DisplayName("회원 탈퇴 성공 케이스: 회원이 존재하면 삭제 호출 확인")
    public void testWithdrawMemberSuccess() {
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        memberWithdrawalService.withdrawMember(memberId);
        verify(memberRepository, times(1)).delete(member); // delete가 한 번 호출되었는지 확인
    }

    /**
     * [메서드 레벨]
     * 회원이 존재하지 않는 경우 예외 처리 테스트
     * - 존재하지 않는 회원 ID로 회원 탈퇴 요청 시 MemberNotFoundException이 발생하는지 검증
     */
    @Test
    @DisplayName("회원 탈퇴 실패 케이스: 회원이 존재하지 않으면 예외 발생")
    public void testWithdrawMemberNotFound() {
        Long memberId = 2L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty()); // 회원이 존재하지 않음

        // 회원이 존재하지 않으면 MemberNotFoundException을 발생시킴
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                memberWithdrawalService.withdrawMember(memberId)
        );
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage()); // 예외 메시지가 예상대로인지 확인
    }
}
