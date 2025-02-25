package com.team1.epilogue.chat.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "participations")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {
  @Id
  private String id;
  private String roomId; //채팅방ID(참조)
  private Long memberId; // 참여한 사용자ID
  private LocalDateTime joinedAt; // 채팅방 참여한 시간

}
