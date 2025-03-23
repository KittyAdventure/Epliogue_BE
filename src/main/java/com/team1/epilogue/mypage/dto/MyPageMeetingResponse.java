package com.team1.epilogue.mypage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageMeetingResponse {
  private int totalPages;
  private List<MeetingDetail> meetings;
}
