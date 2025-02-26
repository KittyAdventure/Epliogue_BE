package com.team1.epilogue.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayResponse {
  private String tid; // 결제 고유 번호, 20자
  // 요청한 클라이언트(Client)가 모바일 앱일 경우
  //카카오톡 결제 페이지 Redirect URL
  @JsonProperty("next_redirect_app_url")
  private String nextRedirectAppUrl;
  //요청한 클라이언트가 모바일 웹일 경우
  //카카오톡 결제 페이지 Redirect URL
  @JsonProperty("next_redirect_mobile_url")
  private String nextRedirectMobileUrl;
  //요청한 클라이언트가 PC 웹일 경우
  //카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한
  // 사용자 정보 입력 화면 Redirect URL
  @JsonProperty("next_redirect_pc_url")
  private String nextRedirectPCUrl;
  //카카오페이 결제 화면으로 이동하는 Android 앱 스킴
  @JsonProperty("android_app_scheme")
  private String androidAppScheme;
  //카카오페이 결제 화면으로 이동하는 iOS 앱 스킴
  @JsonProperty("ios_app_scheme")
  private String iosAppScheme;
  // 결제 준비 요청 시간
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
}
