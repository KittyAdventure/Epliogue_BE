package com.team1.epilogue.chat.entity;


import com.team1.epilogue.common.document.BaseTimeDocument;
import jakarta.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseTimeDocument {
  @Id
  private String id; // MongoDB ObjectId
  private String title; // 책 제목과 동일한 채팅방 이름
  private Set<Long> participants = new HashSet<>();

  // 채팅방에 참여자 추가 (최대 30명 제한)
  public boolean participantsLimit(Long memberId){
    if(participants.size() >= 30){
      return false;
    }

    return participants.add(memberId);
  }

}
