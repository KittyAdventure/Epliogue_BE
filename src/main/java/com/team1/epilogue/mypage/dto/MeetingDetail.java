package com.team1.epilogue.mypage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingDetail {
  private Long meetingId;
  private String meetingBookTitle;
  private int meetingPeople;
  private LocalDateTime dateTime;
  private String location;
  private String thumbnail;
}
