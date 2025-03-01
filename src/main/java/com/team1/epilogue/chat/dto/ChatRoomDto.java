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
  private String roomId;
  private Set<Long> participants;
  private Long bookId;
  private String title;


  public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
    return ChatRoomDto.builder()
        .id(chatRoom.getId())
        .title(chatRoom.getTitle())
        .participants(chatRoom.getParticipants())
        .build();
  }



}