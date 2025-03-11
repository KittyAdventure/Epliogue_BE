package com.team1.epilogue.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentDetail {

  private long commentId;
  private String commentContent;
  private long memberId;
  private String memberNickname;
  private String memberProfile;
  private LocalDateTime commentPostDateTime;
  private int commentLike;
  private String commentColor;
}
