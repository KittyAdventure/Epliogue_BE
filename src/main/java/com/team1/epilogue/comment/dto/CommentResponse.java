package com.team1.epilogue.comment.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponse {
  private int page;
  private int totalPages;
  private List<CommentDetail> comments;

}
