package com.team1.epilogue.gathering.dto;


import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.gathering.entity.Meeting;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDto {

  private Long id;
  private Long memberId;
  private String title;
  private String content;
  private String location;
  private LocalDateTime dateTime;
  private Integer nowPeople;

  public static MeetingDto fromEntity(Meeting meeting) {
    return MeetingDto.builder()
        .id(meeting.getId())
        .memberId(meeting.getMember().getId())
        .title(meeting.getTitle())
        .content(meeting.getContent())
        .location(meeting.getLocation())
        .dateTime(meeting.getDateTime())
        .nowPeople(meeting.getNowPeople())
        .build();
  }

}
