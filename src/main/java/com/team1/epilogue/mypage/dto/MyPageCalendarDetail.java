package com.team1.epilogue.mypage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageCalendarDetail {
  private String thumbnail;
  private String bookTitle;

  private LocalDateTime reviewPostDateTime;
}
