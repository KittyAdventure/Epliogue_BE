package com.team1.epilogue.transaction.service;

import com.team1.epilogue.transaction.client.KakaoPayClient;
import com.team1.epilogue.transaction.dto.KakaoPayApproveResponse;
import com.team1.epilogue.transaction.dto.KakaoPayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoPayService {
  private final KakaoPayClient kakaoPayClient;

  /**
   * 카카오페이 결제 준비 단계 API 호출
   * @param memberId 사용자 ID
   * @param amount 포인트 충전량
   * @return Redirect 할 URL 응답
   */
  public KakaoPayResponse prepareCharge(String memberId, int amount) {
    return kakaoPayClient.prepareCharge(memberId, amount);
  }

  /**
   * 카카오페이 결제 승인 단계 API 호출
   * @param memberId 사용자 ID
   * @param pgToken 카카오에서 발급받은 pg_token
   * @return 카카오서버에서 승인받은 TID 를 담은 객체 return
   */
  public KakaoPayApproveResponse approveCharge(String memberId, String pgToken) {
    return kakaoPayClient.approveCharge(memberId, pgToken);
  }
}
