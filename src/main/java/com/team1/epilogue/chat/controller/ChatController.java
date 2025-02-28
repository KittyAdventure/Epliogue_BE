package com.team1.epilogue.chat.controller;


import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.service.ChatMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/meeting/chat")
public class ChatController {

  private final ChatMessageService chatMessageService;


  /**
   * 채팅방에 메시지 저장
   * @param roomId 채팅방 ID
   * @param memberId 메시지를 보낼 사용자의 ID
   * @param content 메시지 내용
   * @return 저장된 메시지 정보
   */
  @PostMapping("/rooms/{roomId}/messages")
  public ResponseEntity<ChatMessageDto> saveMessage(@PathVariable String roomId,
      @RequestParam Long memberId,
      @RequestParam String content) {
    ChatMessageDto chatMessageDto = ChatMessageDto.builder()
        .roomId(roomId)
        .memberId(memberId)
        .content(content)
        .build();

    ChatMessageDto savedMessage = chatMessageService.saveMessage(chatMessageDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
  }


  /**
   *
   * @param roomId 채팅방 ID
   * @return 채팅방 메세지 조회
   */
  @GetMapping("/room/{roomId}/messages")
  public ResponseEntity<List<ChatMessageDto>> getMessagesByRoom(@PathVariable String roomId) {
    try {
      List<ChatMessageDto> messages = chatMessageService.getMessagesByRoom(roomId);
      return ResponseEntity.ok(messages);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }


}
