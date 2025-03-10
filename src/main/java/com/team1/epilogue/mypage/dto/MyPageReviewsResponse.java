package com.team1.epilogue.mypage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageReviewsResponse {
  private String userNickname;
  private int totalPages;
  private List<MyPageReviewDetail> reviews;
}
