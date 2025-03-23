package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoPayApproveResponse {

  private String aid; // 요청 고유 번호
  private String tid; // 결제 고유 번호
  @JsonProperty("approved_at")
  private LocalDateTime approvedAt; // 결제 승인 시간
}

