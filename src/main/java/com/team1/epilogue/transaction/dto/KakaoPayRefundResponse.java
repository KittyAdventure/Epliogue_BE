package com.team1.epilogue.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoPayRefundResponse {
  private String status;
}
