package com.team1.epilogue.chat.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "participation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationDto {
  @Id
  private String id; //ObjectID
  private String roomId; // 채팅방 ID (참조)
  private Long memberId; // 참여한 사용자 ID
}