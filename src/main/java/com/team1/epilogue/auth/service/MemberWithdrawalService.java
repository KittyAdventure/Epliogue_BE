package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.repositories.jpa.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * [클래스 레벨]
 * MemberWithdrawalService는 회원 탈퇴와 관련된 로직을 처리하는 서비스 클래스
 * 이 클래스는 회원 탈퇴 기능을 담당하며, MemberRepository를 통해 DB 작업을 수행
 */
@Service
@RequiredArgsConstructor
public class MemberWithdrawalService {

    /**
     * [필드 레벨]
     * memberRepository: 회원 정보를 조회하고 삭제하는 데 사용되는 Repository
     * MemberRepository를 통해 회원 정보를 조회하고 삭제 처리
     */
    private final MemberRepository memberRepository;

    /**
     * [메서드 레벨]
     * withdrawMember: 회원 탈퇴 처리 메서드
     * 주어진 회원 ID를 통해 회원을 조회하고, 존재하면 해당 회원을 삭제
     * 존재하지 않는 경우 MemberNotFoundException을 발생시킴
     *
     * @param memberId 탈퇴할 회원의 ID
     */
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new); // 회원이 없으면 예외 발생

        memberRepository.delete(member); // 회원 삭제
    }
}
