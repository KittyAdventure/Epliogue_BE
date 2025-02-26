package com.team1.epilogue.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_messages")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

  @Id
  private String id; // MongoDb ObjectId
  private String roomId; // 메세지가 속한 채팅방 ID
  private Long memberId; // 메세지를 보낸 사용자 ID
  private Long bookId; // 책 ID(채팅방과이름 동일)
  private String content; // 메세지 내용
  private LocalDateTime createdAt; // 메세지 생성 시간
}
