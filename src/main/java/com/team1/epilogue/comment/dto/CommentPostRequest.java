package com.team1.epilogue.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentPostRequest {
  private String memberId;
  private String content;
  private Long reviewId;

}
