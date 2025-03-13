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
  private int memberCnt; // 현재 인원수
  private static final int MAX_MEMBERS = 30;  // 최대 인원
  private Long createId; // 채팅방 생성자

  public boolean canJoin(){
    return memberCnt < MAX_MEMBERS;
  }

  public void addMember() {
    if(canJoin()){
      memberCnt++;
    } else {
      throw new IllegalStateException("채팅방 최대 인원 초과");
    }
  }

  public boolean removeMember() {
    if(memberCnt > 0) {
      memberCnt--;
    }
    return memberCnt == 0;
  }

}
