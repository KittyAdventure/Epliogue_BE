package com.team1.epilogue.mypage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageCommentsResponse {
  private int totalPage;
  private int page;
  private List<MyPageCommentsDetailResponse> comments;
}
