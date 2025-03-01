package com.team1.epilogue.Handler;

import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler {

  private final ChatMessageService chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  @Async // 비동기 처리 지원
  @MessageMapping("/chat.send")
  public void sendMessage(@Payload ChatMessageDto chatMessageDto){
    //메시지 저장
    ChatMessageDto savedMessage = chatMessageService.saveMessage(chatMessageDto);

    // STOMP 브로커를 통해 메세지 전송
    messagingTemplate.convertAndSend("/topic/chat" + savedMessage.getRoomId(), savedMessage);
  }
}
