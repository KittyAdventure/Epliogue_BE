package com.team1.epilogue.transaction.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.repositories.jpa.MemberRepository;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.entity.Transaction;
import com.team1.epilogue.repositories.jpa.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final MemberRepository memberRepository;

  /**
   * 포인트 충전 / 획득 / 사용 을 담당하는 메서드 입니다.
   * 양수 = 충전,획득 / 음수 = 사용
   * @param memberId 사용자 ID
   * @param amount 포인트 변동량
   * @param detail 사용처 , 충전 , 획득처 기록하는 Enum
   * @param tid TransactionDetail 이 KAKAO 일때만 기록되는 카카오페이 취소를 위한 TID (다른때엔 NULL)
   */
  @Transactional
  public void updateBalance(String memberId, int amount, TransactionDetail detail,String tid) {
    // 사용자 정보 가져오기 존재하지않다면 Exception
    Member member = memberRepository.findByLoginId(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    // 거래정보 저장을 위한 Transaction 객체 생성
    Transaction data = Transaction.builder()
        .member(member)
        .amount(amount)
        .afterBalance(member.getPoint() + amount)
        .detail(detail)
        .build();

    if (detail == TransactionDetail.KAKAOPAY) {
      data.setTid(tid);
    }

    // member Point 변경
    member.setPoint(member.getPoint() + amount);

    transactionRepository.save(data); // DB 에 거래내역 저장
    memberRepository.save(member); // 사용자의 포인트 보유량 변경
  }
}
