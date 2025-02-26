package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoPayApproveRequest {

  private String cid;
  private String tid;
  @JsonProperty("partner_order_id")
  private String partnerOrderId;
  @JsonProperty("partner_user_id")
  private String partnerUserId;
  @JsonProperty("pg_token")
  private String pgToken;

  /**
   * 카카오페이 결제 승인을 위한 요청을 만드는 메서드입니다.
   * @param cid 가맹점 번호 (테스트 가맹점 번호)
   * @param tid 결제 준비에서 return 받은 결제번호
   * @param pgToken 카카오에서 보내준 pg_token
   * @param memberId 사용자 ID
   * @return 카카오페이 결제 승인을 위한 Request 객체 return
   */
  public static KakaoPayApproveRequest makeRequest(
      String cid,
      String tid,
      String pgToken,
      String memberId
  ) {
    return KakaoPayApproveRequest.builder()
        .cid(cid)
        .tid(tid)
        .partnerOrderId("1")
        .partnerUserId(memberId)
        .pgToken(pgToken)
        .build();
  }
}
