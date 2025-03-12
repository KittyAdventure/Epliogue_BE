package com.team1.epilogue.mypage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageCalendarResponse {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private String date;
  private int count;
  List<MyPageCalendarDetail> data;
}
