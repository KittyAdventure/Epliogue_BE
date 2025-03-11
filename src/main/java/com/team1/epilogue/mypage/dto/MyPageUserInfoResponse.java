package com.team1.epilogue.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageUserInfoResponse {
  private String nickName;
  private String loginId;
  private String email;
  private int follower;
  private int following;
  private int reviewCount;
  private int commentCount;
  private int meetingCount;
  private int collectionCount;
  private int point;

}
