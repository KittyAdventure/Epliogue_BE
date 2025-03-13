package com.team1.epilogue.gathering.dto;

import com.team1.epilogue.gathering.entity.JoinMeeting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinMeetingResponseDto {

  private Long id;
  private Long memberId;
  private Long meetingId;

  public static JoinMeetingResponseDto fromEntity(JoinMeeting joinMeeting) {
    return new JoinMeetingResponseDto(
        joinMeeting.getId(),
        joinMeeting.getMember().getId(),
        joinMeeting.getMeeting().getId()
    );
  }
}
