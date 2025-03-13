package com.team1.epilogue.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
  private LocalDateTime commentPostDateTime;
  private int commentLike;
  private String commentColor;
}
