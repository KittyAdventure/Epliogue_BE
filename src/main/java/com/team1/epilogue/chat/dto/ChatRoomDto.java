package com.team1.epilogue.chat.dto;

import com.team1.epilogue.chat.entity.ChatRoom;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
  private String id;
  private String title;
  private int memberCnt;
  private Long createId;

}