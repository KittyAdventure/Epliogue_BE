package com.team1.epilogue.chat.controller;


import com.team1.epilogue.chat.entity.ChatMessage;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageService chatMessageService;


  /**
   *  클라이언트가 "/app/chat.sendMessage"로 메세지를 전송
   *  해당 메세지를 DB에 저장 후 "/topic/{roomId}"로 전파
   */
  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/{roomId}")
  public Mono<ChatMessage> sendMessage(ChatMessageDto chatMessageDto){
    return chatMessageService.saveMessage(chatMessageDto.getMemberId()
        ,chatMessageDto.getRoomId()
        ,chatMessageDto.getBookId()
        ,chatMessageDto.getContent());

  }
}
