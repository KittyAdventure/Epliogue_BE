package com.team1.epilogue.transaction.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.dto.TransactionHistoryDto;
import com.team1.epilogue.transaction.dto.TransactionHistoryRequest;
import com.team1.epilogue.transaction.dto.TransactionHistoryResponse;
import com.team1.epilogue.transaction.entity.Transaction;
import com.team1.epilogue.transaction.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final MemberRepository memberRepository;

  /**
   * 포인트 충전 / 획득 / 사용 을 담당하는 메서드 입니다. 양수 = 충전,획득 / 음수 = 사용
   *
   * @param memberId 사용자 ID
   * @param amount   포인트 변동량
   * @param detail   사용처 , 충전 , 획득처 기록하는 Enum
   * @param tid      TransactionDetail 이 KAKAO 일때만 기록되는 카카오페이 취소를 위한 TID (다른때엔 NULL)
   */
  @Transactional
  public void updateBalance(String memberId, int amount, TransactionDetail detail, String tid) {
    // 사용자 정보 가져오기 존재하지않다면 Exception
    Member member = memberRepository.findByLoginIdWithLock(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    // 거래정보 저장을 위한 Transaction 객체 생성
    Transaction data = Transaction.builder()
        .member(member)
        .amount(amount)
        .afterBalance(member.getPoint() + amount)
        .detail(detail)
        .build();

    if (detail == TransactionDetail.KAKAOPAY) {
      data = data.toBuilder().tid(tid).build();
    }

    // member Point 변경
    member.setPoint(member.getPoint() + amount);

    transactionRepository.save(data); // DB 에 거래내역 저장
    memberRepository.save(member); // 사용자의 포인트 보유량 변경
  }

  /**
   * 회원의 거래내역을 조회하는 메서드입니다.
   * @param dto 시작할 날짜와 끝나는 날짜 , 페이지 데이터 갯수제한, 페이지 번호를 담은 DTO
   * @return TransactionHistoryResponse 객체 return
   */
  public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest dto) {
    // 시작 날짜 설정
    LocalDateTime start = dto.getStartDate().atStartOfDay();
    // 끝나는 날짜 설정
    LocalDateTime end = dto.getEndDate().atTime(23, 59, 59);
    // 페이징 설정 -> 프론트에서 넘어온 페이지 번호와, 한 페이지 데이터 갯수 설정
    Pageable pageable = PageRequest.of(dto.getPage(), dto.getLimit());

    // Repository 에서 거래내역 가져오기
    Page<Transaction> response = transactionRepository
        .findByDateTimeBetween(start, end, pageable);

    // 데이터 API 명세서에 맞게 가공해서 return
    return TransactionHistoryResponse.builder()
        .total(response.getNumberOfElements())
        .limit(response.getSize())
        .page(dto.getPage())
        // Page 객체 내부의 Transaction Entity -> DTO 로 변환
        .data(response.getContent().stream().map(data -> TransactionHistoryDto.toDto(data))
        .collect(Collectors.toList()))
        .build();
  }
}
