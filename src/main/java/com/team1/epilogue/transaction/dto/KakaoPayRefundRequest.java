package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoPayRefundRequest {

  //가맹점 코드
  private String cid;
  // 결제 고유번호
  private String tid;
  //취소 금액
  @JsonProperty("cancel_amount")
  private int cancelAmount;
  // 취소 비과세 금액
  @JsonProperty("cancel_tax_free_amount")
  private int cancelTaxFreeAmount;

  public static KakaoPayRefundRequest makeRequest(
      String cid,
      String tid,
      int cancelAmount,
      int cancelTaxFreeAmount
  ) {
    return KakaoPayRefundRequest.builder()
        .cid(cid)
        .tid(tid)
        .cancelAmount(cancelAmount)
        .cancelTaxFreeAmount(cancelTaxFreeAmount)
        .build();
  }
}
