package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class KakaoPayRequest {

  private String cid;
  @JsonProperty("partner_order_id")
  private String partnerOrderId;// 가맹점 주문번호, 최대 100자
  @JsonProperty("partner_user_id")
  private String partnerUserId; // 가맹점 회원 id, 최대 100자
  // (실명, ID와 같은 개인정보가 포함되지 않도록 유의)
  @JsonProperty("item_name")
  private String itemName; // 상품명 최대 100자
  private int quantity; // 상품 수량
  @JsonProperty("total_amount")
  private int totalAmount; // 상품 총액
  @JsonProperty("tax_free_amount")
  private int taxFreeAmount; // 상품 비과세 금액
  @JsonProperty("approval_url")
  private String approvalUrl; // 결제 성공시 url
  @JsonProperty("cancel_url")
  private String cancelUrl; // 결제 취소시 url
  @JsonProperty("fail_url")
  private String failUrl; // 결제 실패시 url

  /**
   * 카카오페이 결제 준비 API 를 위해 요청을 만드는 메서드입니다.
   * @param cid 가맹점 번호 (테스트 가맹점 번호 입력할 것)
   * @param memberId 사용자 ID
   * @param amount 결제하는 금액
   * @param approvalUrl 성공시 redirect 할 URL
   * @param cancelUrl 취소시 redirect 할 URL
   * @param failUrl 실패시 redirect 할 URL
   * @return 카카오페이 결제 준비를 위한 Request 객체 return
   */
  public static KakaoPayRequest makeRequest(
      String cid,
      String memberId,
      int amount,
      String approvalUrl,
      String cancelUrl,
      String failUrl) {

    return KakaoPayRequest.builder()
        .cid(cid)
        .partnerOrderId("1")
        .partnerUserId(memberId)
        .itemName("포인트 충전")
        .quantity(1)
        .totalAmount(amount)
        .taxFreeAmount(0)
        .approvalUrl(approvalUrl + "?member_id=" + memberId + "&amount=" + amount)
        .cancelUrl(cancelUrl)
        .failUrl(failUrl)
        .build();
  }
}
