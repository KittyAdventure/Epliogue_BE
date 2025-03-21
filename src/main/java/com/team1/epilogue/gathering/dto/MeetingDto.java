package com.team1.epilogue.gathering.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
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
  private String bookId;
  private String title;
  private String content;
  private String location;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dateTime;
  private Integer nowPeople;
  private String bookImage;
  private String bookTitle;

  public static MeetingDto fromEntity(Meeting meeting) {
    return MeetingDto.builder()
        .id(meeting.getId())
        .memberId(meeting.getMember().getId())
        .bookId(meeting.getBook().getId())
        .bookImage(meeting.getBook().getCoverUrl())
        .bookTitle(meeting.getBook().getTitle())
        .title(meeting.getTitle())
        .content(meeting.getContent())
        .location(meeting.getLocation())
        .dateTime(meeting.getDateTime())
        .nowPeople(meeting.getNowPeople())
        .build();
  }

}
