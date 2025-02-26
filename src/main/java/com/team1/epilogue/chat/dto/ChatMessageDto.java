package com.team1.epilogue.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
  private String roomId;
  private Long memberId;
  private String content;
  private LocalDateTime createdAt;
}
