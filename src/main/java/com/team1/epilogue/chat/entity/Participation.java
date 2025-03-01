package com.team1.epilogue.chat.entity;


import com.team1.epilogue.common.document.BaseTimeDocument;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "participations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation extends BaseTimeDocument {
  @Id
  private String id;
  private String roomId; //채팅방ID(참조)
  private Long memberId; // 참여한 사용자ID
}
