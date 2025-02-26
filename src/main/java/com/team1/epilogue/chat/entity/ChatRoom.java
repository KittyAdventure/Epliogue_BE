package com.team1.epilogue.chat.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_rooms")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
  @Id
  private String id; // MongoDB ObjectId
  private Long bookId; // 책 ID
  private String title; // 책 제목과 동일한 채팅방 이름
  private LocalDateTime createAt; // 채팅방 생성 일시
}
