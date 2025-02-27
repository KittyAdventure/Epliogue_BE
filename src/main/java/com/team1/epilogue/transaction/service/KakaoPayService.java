package com.team1.epilogue.transaction.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.transaction.client.KakaoPayClient;
import com.team1.epilogue.transaction.domain.TransactionDetail;
import com.team1.epilogue.transaction.dto.KakaoPayApproveResponse;
import com.team1.epilogue.transaction.dto.KakaoPayResponse;
import com.team1.epilogue.transaction.entity.Transaction;
import com.team1.epilogue.transaction.exception.InvalidRequestException;
import com.team1.epilogue.transaction.exception.InvalidTransactionException;
import com.team1.epilogue.transaction.exception.TransactionNotFoundException;
import com.team1.epilogue.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

  private final KakaoPayClient kakaoPayClient;
  private final MemberRepository memberRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionService transactionService;

  @Value("${kakao.pay.url}")
  private String kakaoPayUrl;

  /**
   * 카카오페이 결제 준비 단계 API 호출
   *
   * @param memberId 사용자 ID
   * @param amount   포인트 충전량
   * @return Redirect 할 URL 응답
   */
  public KakaoPayResponse prepareCharge(String memberId, int amount) {
    return kakaoPayClient.prepareCharge(kakaoPayUrl,memberId, amount);
  }

  /**
   * 카카오페이 결제 승인 단계 API 호출
   *
   * @param memberId 사용자 ID
   * @param pgToken  카카오에서 발급받은 pg_token
   * @return 카카오서버에서 승인받은 TID 를 담은 객체 return
   */
  public KakaoPayApproveResponse approveCharge(String memberId, String pgToken) {
    return kakaoPayClient.approveCharge(kakaoPayUrl,memberId, pgToken);
  }

  /**
   * 카카오페이 환불 요청 API
   * @param memberId 사용자 ID
   * @param transactionId DB 에 저장되어 있는 거래 데이터 PK
   */
  public void kakaoPayRefund(String memberId, Long transactionId) {
    // 회원정보를 가져온다. 회원정보가 존재하지 않을땐 예외 발생
    Member member = memberRepository.findByLoginId(memberId)
        .orElseThrow(() -> new MemberNotFoundException());

    // 거래 정보를 가져온다. 거래정보가 존재하지 않을땐 예외 발생
    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new TransactionNotFoundException("존재하지 않는 거래내역입니다."));

    // 환불하려는 포인트보다 사용자의 포인트 보유량이 적다면 예외 발생
    // 거래 정보가 카카오페이 충전 기록이 아니어도 예외 발생
    if (member.getPoint() < transaction.getAmount() || transaction.getDetail() != TransactionDetail.KAKAOPAY) {
      throw new InvalidTransactionException("유효하지 않은 환불 요청입니다.");
    }

    // kakaoPayClient 에서 카카오페이 서버로 요청을 보낸다.
    String response = kakaoPayClient.refund(kakaoPayUrl,transaction.getTid(), transaction.getAmount());

    // 카카오에서 응답해준 status 가 CANCEL_PAYMENT 가 아니라면 예외 발생
    if (!response.equals("CANCEL_PAYMENT")) {
      throw new InvalidRequestException("취소 요청이 실패했습니다.");
    }

    // 사용자의 포인트 보유량 정보 업데이트.(환불 했으니, 환불한 금액만큼 차감)
    transactionService.updateBalance(memberId, transaction.getAmount() * -1, TransactionDetail.CANCEL,
        null);
  }
}
