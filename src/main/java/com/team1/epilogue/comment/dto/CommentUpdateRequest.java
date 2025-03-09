package com.team1.epilogue.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentUpdateRequest {
  private Long commentId;
  private String content;

}
