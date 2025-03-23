package com.team1.epilogue.chat.entity;

import com.team1.epilogue.common.document.BaseTimeDocument;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatMessage extends BaseTimeDocument {
  @Id
  private String id; // MongoDb ObjectId
  private String roomId; // 메세지가 속한 채팅방 ID
  private Long memberId; // 메세지를 보낸 사용자 ID
  private String content; // 메세지 내용
}
