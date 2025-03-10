package com.team1.epilogue.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageReviewDetail {
  private long reviewId;
  private String reviewBookTitle;
  private int reviewBookPubYear;
  private String reviewBookAuthor;
  private String reviewContent;
  private int reviewCommentsCount;

}
