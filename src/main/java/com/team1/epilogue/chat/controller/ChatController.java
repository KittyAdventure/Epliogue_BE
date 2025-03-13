package com.team1.epilogue.chat.controller;


import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.service.ChatMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting/chat")
@Slf4j
public class ChatController {

  private final ChatMessageService chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  
  //채팅보내기
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessageDto chatMessageDto){
    ChatMessageDto savedMessage = chatMessageService.saveMessage(chatMessageDto);
    messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getRoomId(), savedMessage);
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
