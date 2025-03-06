package com.team1.epilogue.chat.entity;


import com.team1.epilogue.common.document.BaseTimeDocument;
import jakarta.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatRoom extends BaseTimeDocument {
  @Id
  private String id; // MongoDB ObjectId
  private String title; // 책 제목과 동일한 채팅방 이름
  private Set<Long> participants = new HashSet<>();

  // 채팅방에 참여자 추가 (최대 30명 제한)
  public boolean participantsLimit(Long memberId) {
    if(participants.size() >= 30){
      return false;  // 30명 초과 시 참여자 추가 불가
    }
    // 추가 전 중복 여부를 확인
    if (!participants.contains(memberId)) {
      return participants.add(memberId);  // 중복되지 않으면 추가
    }
    return false;  // 이미 존재하면 추가하지 않음
  }

}
