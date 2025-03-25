package com.team1.epilogue.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageUserInfo {
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
  private String profileUrl;

}
