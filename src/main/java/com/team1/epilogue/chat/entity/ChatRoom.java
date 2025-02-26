package com.team1.epilogue.chat.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
  @Id
  private String id; // MongoDB ObjectId
  private String title; // 책 제목과 동일한 채팅방 이름
  private Set<Long> participants = new HashSet<>();
  private LocalDateTime createAt; // 채팅방 생성 일시

  // 채팅방에 참여자 추가 (최대 30명 제한)
  public boolean participantsLimit(Long memberId){
    if(participants.size() >= 30){
      return false;
    }

    return participants.add(memberId);
  }

  // 채팅방에서 특정 사용자 제거
//  public void removeParticipant(Long memberId){
//    participants.remove(memberId);
//  }


}
