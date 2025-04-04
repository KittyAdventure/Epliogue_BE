package com.team1.epilogue.chat.dto;

import com.team1.epilogue.chat.entity.Participation;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationDto {
  @Id
  private String id; //ObjectID
  private String roomId; // 채팅방 ID (참조)
  private Long memberId; // 참여한 사용자 ID

  public static ParticipationDto fromEntity(Participation participation) {
    return ParticipationDto.builder()
        .id(participation.getId())
        .roomId(participation.getRoomId())
        .memberId(participation.getMemberId())
        .build();

  }
}