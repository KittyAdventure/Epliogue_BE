package com.team1.epilogue.chat.dto;

import com.team1.epilogue.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
  private String id;
  private String roomId;
  private Long memberId;
  private String content;


  public ChatMessage fromEntity() {
    return ChatMessage.builder()
        .id(id)
        .memberId(memberId)
        .roomId(roomId)
        .content(content)
        .build();
  }

}
