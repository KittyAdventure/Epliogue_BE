package com.team1.epilogue.alarm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlarmResponse {
  private Long reviewId;
  private String content;

}
